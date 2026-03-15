package com.example.ccrewards.data.db.converters;

import androidx.room.TypeConverter;

import com.example.ccrewards.data.model.FreeNightLimitType;
import com.example.ccrewards.data.model.HotelGroup;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.ResetType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.TransferPartnerType;

import java.time.LocalDate;

public class Converters {

    @TypeConverter
    public static Long fromLocalDate(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }

    @TypeConverter
    public static LocalDate toLocalDate(Long epochDay) {
        return epochDay == null ? null : LocalDate.ofEpochDay(epochDay);
    }

    @TypeConverter
    public static String fromRewardCategory(RewardCategory category) {
        return category == null ? null : category.name();
    }

    @TypeConverter
    public static RewardCategory toRewardCategory(String name) {
        return name == null ? null : RewardCategory.valueOf(name);
    }

    @TypeConverter
    public static String fromRateType(RateType rateType) {
        return rateType == null ? null : rateType.name();
    }

    @TypeConverter
    public static RateType toRateType(String name) {
        return name == null ? null : RateType.valueOf(name);
    }

    @TypeConverter
    public static String fromResetPeriod(ResetPeriod period) {
        return period == null ? null : period.name();
    }

    @TypeConverter
    public static ResetPeriod toResetPeriod(String name) {
        return name == null ? null : ResetPeriod.valueOf(name);
    }

    @TypeConverter
    public static String fromTransferPartnerType(TransferPartnerType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static TransferPartnerType toTransferPartnerType(String name) {
        return name == null ? null : TransferPartnerType.valueOf(name);
    }

    @TypeConverter
    public static String fromResetType(ResetType resetType) {
        return resetType == null ? null : resetType.name();
    }

    @TypeConverter
    public static ResetType toResetType(String name) {
        return name == null ? null : ResetType.valueOf(name);
    }

    @TypeConverter
    public static String fromHotelGroup(HotelGroup group) {
        return group == null ? null : group.name();
    }

    @TypeConverter
    public static HotelGroup toHotelGroup(String name) {
        return name == null ? null : HotelGroup.valueOf(name);
    }

    @TypeConverter
    public static String fromFreeNightLimitType(FreeNightLimitType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static FreeNightLimitType toFreeNightLimitType(String name) {
        return name == null ? null : FreeNightLimitType.valueOf(name);
    }
}
