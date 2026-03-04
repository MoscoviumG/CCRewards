package com.example.ccrewards.ui.mycards;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.db.dao.CardBenefitDao;
import com.example.ccrewards.data.db.dao.CardDefinitionDao;
import com.example.ccrewards.data.db.dao.RewardRateDao;
import com.example.ccrewards.data.db.dao.UserCardDao;
import com.example.ccrewards.data.db.dao.WelcomeBonusDao;
import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.repository.RewardRateRepository;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.model.CustomCategoryRate;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.ResetType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.repository.CustomCategoryRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCustomCardViewModel extends ViewModel {

    private final CustomCategoryRepository customCategoryRepository;
    private final RewardRateRepository rewardRateRepository;
    private final CardDefinitionDao cardDefinitionDao;
    private final RewardRateDao rewardRateDao;
    private final CardBenefitDao cardBenefitDao;
    private final UserCardDao userCardDao;
    private final WelcomeBonusDao welcomeBonusDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    final MutableLiveData<List<PendingRate>> pendingRates = new MutableLiveData<>(new ArrayList<>());
    final MutableLiveData<List<PendingBenefit>> pendingBenefits = new MutableLiveData<>(new ArrayList<>());
    private WelcomeBonus pendingWelcomeBonus;

    static class PendingRate {
        @Nullable RewardCategory rewardCategory; // null → custom category
        long customCategoryId;                    // valid only when rewardCategory == null
        String displayName;
        double rate;
        RateType rateType = RateType.POINTS;
        String currencyName = "";                 // used for CUSTOM category rates
    }

    static class PendingBenefit {
        String name;
        int amountCents;
        ResetPeriod resetPeriod = ResetPeriod.ANNUALLY;
        ResetType resetType = ResetType.CALENDAR;
    }

    @Inject
    public AddCustomCardViewModel(CustomCategoryRepository customCategoryRepository,
                                  RewardRateRepository rewardRateRepository,
                                  CardDefinitionDao cardDefinitionDao,
                                  RewardRateDao rewardRateDao,
                                  CardBenefitDao cardBenefitDao,
                                  UserCardDao userCardDao,
                                  WelcomeBonusDao welcomeBonusDao) {
        this.customCategoryRepository = customCategoryRepository;
        this.rewardRateRepository = rewardRateRepository;
        this.cardDefinitionDao = cardDefinitionDao;
        this.rewardRateDao = rewardRateDao;
        this.cardBenefitDao = cardBenefitDao;
        this.userCardDao = userCardDao;
        this.welcomeBonusDao = welcomeBonusDao;
    }

    /** Sync — call from background thread only. */
    public List<PointValuation> getAllValuationsSync() {
        return rewardRateRepository.getAllValuationsSync();
    }

    public void insertValuationSync(PointValuation pv, Runnable onDone) {
        executor.execute(() -> {
            rewardRateRepository.insertValuationSync(pv);
            if (onDone != null) onDone.run();
        });
    }

    public LiveData<List<CustomCategory>> getCustomCategories() {
        return customCategoryRepository.getAllCustomCategoriesLive();
    }

    public List<CustomCategory> getAllCustomCategoriesSync() {
        return customCategoryRepository.getAllCustomCategoriesSync();
    }

    public void addRate(PendingRate rate) {
        List<PendingRate> list = new ArrayList<>(safeList(pendingRates));
        list.add(rate);
        pendingRates.setValue(list);
    }

    public void removeRate(int index) {
        List<PendingRate> list = new ArrayList<>(safeList(pendingRates));
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            pendingRates.setValue(list);
        }
    }

    public void addBenefit(PendingBenefit benefit) {
        List<PendingBenefit> list = new ArrayList<>(safeList(pendingBenefits));
        list.add(benefit);
        pendingBenefits.setValue(list);
    }

    public void removeBenefit(int index) {
        List<PendingBenefit> list = new ArrayList<>(safeList(pendingBenefits));
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            pendingBenefits.setValue(list);
        }
    }

    public void setPendingWelcomeBonus(WelcomeBonus wb) { pendingWelcomeBonus = wb; }
    public void clearPendingWelcomeBonus() { pendingWelcomeBonus = null; }
    public WelcomeBonus getPendingWelcomeBonus() { return pendingWelcomeBonus; }

    public void saveCard(String name, String issuer, int annualFee, String currencyName,
                         LocalDate openDate, String nickname, int creditLimit,
                         Runnable onComplete) {
        List<PendingRate> rates = new ArrayList<>(safeList(pendingRates));
        List<PendingBenefit> benefits = new ArrayList<>(safeList(pendingBenefits));
        WelcomeBonus wb = pendingWelcomeBonus;

        executor.execute(() -> {
            String cardId = "custom_" + System.currentTimeMillis();
            String effectiveName = name.isEmpty() ? "Custom Card" : name;
            String effectiveIssuer = issuer.isEmpty() ? "Custom" : issuer;
            String effectiveCurrency = currencyName.isEmpty() ? "Cash Back" : currencyName;

            CardDefinition def = new CardDefinition(cardId, effectiveName, effectiveIssuer,
                    "Visa", annualFee, true, false, 0xFF607D8BL, 0xFF90A4AEL, effectiveCurrency);
            cardDefinitionDao.insert(def);

            for (PendingRate pr : rates) {
                if (pr.rewardCategory != null) {
                    RewardRate rr = new RewardRate(cardId, pr.rewardCategory, pr.rateType, pr.rate);
                    rr.isCustomized = true;
                    rewardRateDao.insert(rr);
                } else {
                    customCategoryRepository.upsertRateSync(
                            new CustomCategoryRate(pr.customCategoryId, cardId, pr.rate, pr.rateType,
                                    pr.currencyName));
                }
            }

            for (PendingBenefit pb : benefits) {
                cardBenefitDao.insert(new CardBenefit(cardId, pb.name, null,
                        pb.amountCents, pb.resetPeriod, true, pb.resetType));
            }

            Integer maxOrder = userCardDao.getMaxSortOrder();
            UserCard uc = new UserCard(cardId,
                    (nickname != null && !nickname.isEmpty()) ? nickname : null,
                    creditLimit, openDate, null, 0);
            uc.sortOrder = (maxOrder == null ? 0 : maxOrder + 1);
            long newId = userCardDao.insert(uc);

            if (wb != null) {
                wb.userCardId = newId;
                welcomeBonusDao.upsert(wb);
            }

            if (onComplete != null) onComplete.run();
        });
    }

    private static <T> List<T> safeList(MutableLiveData<List<T>> liveData) {
        List<T> v = liveData.getValue();
        return v != null ? v : new ArrayList<>();
    }
}
