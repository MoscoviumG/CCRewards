package com.example.ccrewards.ui.settings;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.repository.CustomCategoryRepository;
import com.example.ccrewards.util.CategoryDisplayPrefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class ManageCategoriesViewModel extends ViewModel {

    public static class CategoryItem {
        public final String key;
        public final String label;
        public final boolean isCustom;
        public final long customId;
        public boolean visible;

        public CategoryItem(String key, String label, boolean isCustom, long customId, boolean visible) {
            this.key = key;
            this.label = label;
            this.isCustom = isCustom;
            this.customId = customId;
            this.visible = visible;
        }
    }

    private final Context context;
    private final CustomCategoryRepository customCategoryRepo;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<CategoryItem>> items = new MutableLiveData<>(new ArrayList<>());

    @Inject
    public ManageCategoriesViewModel(@ApplicationContext Context context,
                                     CustomCategoryRepository customCategoryRepo) {
        this.context = context;
        this.customCategoryRepo = customCategoryRepo;
        reload();
    }

    public LiveData<List<CategoryItem>> getItems() {
        return items;
    }

    public void reload() {
        executor.execute(() -> {
            List<CustomCategory> customs = customCategoryRepo.getAllCustomCategoriesSync();
            List<Long> customIds = new ArrayList<>();
            Map<Long, String> customLabels = new HashMap<>();
            for (CustomCategory c : customs) {
                customIds.add(c.id);
                customLabels.put(c.id, c.name);
            }

            List<String> orderedKeys = CategoryDisplayPrefs.getMergedOrderedKeys(context, customIds);
            Set<String> hiddenKeys = CategoryDisplayPrefs.getHiddenKeys(context);

            List<CategoryItem> result = new ArrayList<>();
            for (String key : orderedKeys) {
                boolean visible = !hiddenKeys.contains(key);
                if (CategoryDisplayPrefs.isCustomKey(key)) {
                    long id = CategoryDisplayPrefs.customIdFromKey(key);
                    String label = customLabels.get(id);
                    if (label != null) {
                        result.add(new CategoryItem(key, label, true, id, visible));
                    }
                } else {
                    try {
                        com.example.ccrewards.data.model.RewardCategory cat =
                                CategoryDisplayPrefs.builtinCategoryFromKey(key);
                        String label = CategoryDisplayPrefs.labelForBuiltin(cat);
                        result.add(new CategoryItem(key, label, false, -1L, visible));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            items.postValue(result);
        });
    }

    /** Called after a drag completes — adapter already has the new order, just persist it. */
    public void persistOrderFromAdapter(List<CategoryItem> adapterItems) {
        persistOrder(adapterItems);
    }

    public void setVisible(String key, boolean visible) {
        CategoryDisplayPrefs.setVisible(context, key, visible);
        List<CategoryItem> current = items.getValue();
        if (current == null) return;
        for (CategoryItem item : current) {
            if (item.key.equals(key)) {
                item.visible = visible;
                break;
            }
        }
        items.setValue(current);
    }

    public void deleteCustomCategory(long id) {
        String key = CategoryDisplayPrefs.customKey(id);
        List<CategoryItem> current = items.getValue();
        if (current != null) {
            current.removeIf(item -> item.key.equals(key));
            items.setValue(current);
            persistOrder(current);
        }
        // Remove from hidden set if present
        CategoryDisplayPrefs.setVisible(context, key, true);
        executor.execute(() -> customCategoryRepo.deleteCategorySync(id));
    }

    public void addCustomCategory(String name, java.util.function.Consumer<Long> onCreated) {
        customCategoryRepo.insertCategory(name, newId -> {
            String key = CategoryDisplayPrefs.customKey(newId);
            List<CategoryItem> current = new ArrayList<>(
                    items.getValue() != null ? items.getValue() : new ArrayList<>());
            current.add(new CategoryItem(key, name, true, newId, true));
            items.postValue(current);
            persistOrder(current);
            if (onCreated != null) onCreated.accept(newId);
        });
    }

    private void persistOrder(List<CategoryItem> list) {
        List<String> keys = new ArrayList<>();
        for (CategoryItem item : list) keys.add(item.key);
        CategoryDisplayPrefs.saveOrder(context, keys);
    }
}
