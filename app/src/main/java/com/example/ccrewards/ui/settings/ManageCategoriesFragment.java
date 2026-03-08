package com.example.ccrewards.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.R;
import com.example.ccrewards.databinding.FragmentManageCategoriesBinding;
import com.example.ccrewards.databinding.ItemManageCategoryBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ManageCategoriesFragment extends Fragment {

    private FragmentManageCategoriesBinding binding;
    private ManageCategoriesViewModel viewModel;
    private CategoryAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentManageCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ManageCategoriesViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        adapter = new CategoryAdapter();
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCategories.setAdapter(adapter);

        // Delete listener — ViewModel handles DB delete + LiveData update
        adapter.setDeleteListener(id -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete category?")
                    .setMessage("This category and all its custom rates will be removed.")
                    .setPositiveButton("Delete", (d, w) -> viewModel.deleteCustomCategory(id))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // ItemTouchHelper for drag-to-reorder (only via drag handle)
        ItemTouchHelper.SimpleCallback dragCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView rv,
                                         @NonNull RecyclerView.ViewHolder from,
                                         @NonNull RecyclerView.ViewHolder to) {
                        adapter.moveItem(from.getAdapterPosition(), to.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) { }

                    @Override
                    public void clearView(@NonNull RecyclerView rv,
                                         @NonNull RecyclerView.ViewHolder vh) {
                        super.clearView(rv, vh);
                        // Adapter already has the final order — just persist it
                        viewModel.persistOrderFromAdapter(adapter.getItems());
                    }

                    @Override
                    public boolean isLongPressDragEnabled() { return false; }
                };
        itemTouchHelper = new ItemTouchHelper(dragCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerCategories);

        adapter.setDragStartListener(holder -> itemTouchHelper.startDrag(holder));

        viewModel.getItems().observe(getViewLifecycleOwner(), items -> adapter.setItems(items));

        // FAB → add custom category
        binding.fabAddCategory.setOnClickListener(v -> showAddCategoryDialog(view));
    }

    private void showAddCategoryDialog(View navView) {
        EditText input = new EditText(requireContext());
        input.setHint("Category name (e.g., Apple.com)");
        input.setPadding(48, 24, 48, 0);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Custom Category")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText() != null
                            ? input.getText().toString().trim() : "";
                    if (name.isEmpty()) return;
                    viewModel.addCustomCategory(name, newId -> {
                        requireActivity().runOnUiThread(() -> {
                            Bundle args = new Bundle();
                            args.putLong("customCategoryId", newId);
                            Navigation.findNavController(navView)
                                    .navigate(R.id.action_manageCategories_to_customCategoryDetail, args);
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Adapter ──────────────────────────────────────────────────────────────

    static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

        interface DragStartListener {
            void onStartDrag(RecyclerView.ViewHolder holder);
        }

        interface DeleteListener {
            void onDelete(long customCategoryId);
        }

        private List<ManageCategoriesViewModel.CategoryItem> items = new ArrayList<>();
        private DragStartListener dragStartListener;
        private DeleteListener deleteListener;

        void setItems(List<ManageCategoriesViewModel.CategoryItem> data) {
            items = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        List<ManageCategoriesViewModel.CategoryItem> getItems() {
            return new ArrayList<>(items);
        }

        void setDragStartListener(DragStartListener l) { dragStartListener = l; }
        void setDeleteListener(DeleteListener l) { deleteListener = l; }

        void moveItem(int from, int to) {
            ManageCategoriesViewModel.CategoryItem moved = items.remove(from);
            items.add(to, moved);
            notifyItemMoved(from, to);
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemManageCategoryBinding b = ItemManageCategoryBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ManageCategoriesViewModel.CategoryItem item = items.get(position);
            holder.binding.tvCategoryName.setText(item.label);

            holder.binding.switchVisible.setOnCheckedChangeListener(null);
            holder.binding.switchVisible.setChecked(item.visible);
            holder.binding.switchVisible.setOnCheckedChangeListener((btn, checked) -> {
                item.visible = checked;
                com.example.ccrewards.util.CategoryDisplayPrefs.setVisible(
                        btn.getContext(), item.key, checked);
            });

            if (item.isCustom) {
                holder.binding.btnDeleteCategory.setVisibility(View.VISIBLE);
                holder.binding.btnDeleteCategory.setOnClickListener(v -> {
                    if (deleteListener != null) deleteListener.onDelete(item.customId);
                });
            } else {
                holder.binding.btnDeleteCategory.setVisibility(View.GONE);
            }

            holder.binding.dragHandle.setOnTouchListener((v, event) -> {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (dragStartListener != null) dragStartListener.onStartDrag(holder);
                }
                return false;
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemManageCategoryBinding binding;
            VH(ItemManageCategoryBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
