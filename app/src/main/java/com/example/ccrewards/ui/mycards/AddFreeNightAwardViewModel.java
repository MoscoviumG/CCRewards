package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.FreeNightAward;
import com.example.ccrewards.data.model.FreeNightLimitType;
import com.example.ccrewards.data.model.HotelGroup;
import com.example.ccrewards.data.repository.FreeNightRepository;

import java.time.LocalDate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddFreeNightAwardViewModel extends ViewModel {

    private final FreeNightRepository repository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<FreeNightAward> existingAward = new MutableLiveData<>();

    @Inject
    public AddFreeNightAwardViewModel(FreeNightRepository repository) {
        this.repository = repository;
    }

    public LiveData<FreeNightAward> getExistingAward() { return existingAward; }

    public void loadAward(long awardId) {
        executor.execute(() -> existingAward.postValue(repository.getAwardByIdSync(awardId)));
    }

    public void saveAward(long userCardId, HotelGroup hotelGroup,
                          FreeNightLimitType limitType, Integer pointsCap, Integer hyattCategory,
                          String label, LocalDate expirationDate, int count,
                          Runnable onComplete) {

        String typeKey = buildTypeKey(hotelGroup, limitType, pointsCap, hyattCategory);

        FreeNightAward award = new FreeNightAward(
                userCardId, typeKey,
                (label != null && !label.isEmpty()) ? label : null,
                expirationDate, count, false);

        repository.insertAward(award, onComplete);
    }

    public void updateAward(long awardId, HotelGroup hotelGroup,
                            FreeNightLimitType limitType, Integer pointsCap, Integer hyattCategory,
                            String label, LocalDate expirationDate, int count,
                            Runnable onComplete) {

        FreeNightAward existing = existingAward.getValue();
        if (existing == null) return;

        existing.typeKey = buildTypeKey(hotelGroup, limitType, pointsCap, hyattCategory);
        existing.label = (label != null && !label.isEmpty()) ? label : null;
        existing.expirationDate = expirationDate;
        existing.totalCount = count;

        executor.execute(() -> {
            repository.updateAward(existing);
            if (onComplete != null) onComplete.run();
        });
    }

    static String buildTypeKey(HotelGroup hotelGroup, FreeNightLimitType limitType,
                               Integer pointsCap, Integer hyattCategory) {
        switch (limitType) {
            case UNLIMITED:
                return hotelGroup.name() + "_UNLIMITED";
            case POINTS_CAP:
                return hotelGroup.name() + "_" + (pointsCap != null ? pointsCap : 0);
            case HYATT_CATEGORY:
                return "HYATT_CAT_" + (hyattCategory != null ? hyattCategory : 1);
            default:
                return hotelGroup.name() + "_UNLIMITED";
        }
    }

    /** Parses a typeKey back into its components for pre-populating the edit form. */
    static ParsedTypeKey parseTypeKey(String typeKey) {
        if (typeKey == null) return null;
        if (typeKey.startsWith("HYATT_CAT_")) {
            try {
                int cat = Integer.parseInt(typeKey.substring("HYATT_CAT_".length()));
                return new ParsedTypeKey(HotelGroup.HYATT, FreeNightLimitType.HYATT_CATEGORY, null, cat);
            } catch (NumberFormatException ignored) {}
        }
        if (typeKey.endsWith("_UNLIMITED")) {
            String groupName = typeKey.substring(0, typeKey.length() - "_UNLIMITED".length());
            try {
                return new ParsedTypeKey(HotelGroup.valueOf(groupName),
                        FreeNightLimitType.UNLIMITED, null, null);
            } catch (IllegalArgumentException ignored) {}
        }
        int last = typeKey.lastIndexOf('_');
        if (last > 0) {
            try {
                String groupName = typeKey.substring(0, last);
                int cap = Integer.parseInt(typeKey.substring(last + 1));
                return new ParsedTypeKey(HotelGroup.valueOf(groupName),
                        FreeNightLimitType.POINTS_CAP, cap, null);
            } catch (Exception ignored) {}
        }
        return null;
    }

    static class ParsedTypeKey {
        final HotelGroup hotelGroup;
        final FreeNightLimitType limitType;
        final Integer pointsCap;
        final Integer hyattCategory;

        ParsedTypeKey(HotelGroup hotelGroup, FreeNightLimitType limitType,
                      Integer pointsCap, Integer hyattCategory) {
            this.hotelGroup = hotelGroup;
            this.limitType = limitType;
            this.pointsCap = pointsCap;
            this.hyattCategory = hyattCategory;
        }
    }
}
