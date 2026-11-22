package me.konsolas.aac.compat;

import com.comphenix.protocol.reflect.EquivalentConverter;

public class EnumPacketConverter<T extends Enum<T>>
implements EquivalentConverter<T> {
    private final Class<T> specificType;

    public EnumPacketConverter(Class<T> clazz) {
        this.specificType = clazz;
    }

    public T toSpecificEnum(Object obj) {
        return Enum.valueOf(this.specificType, ((Enum<?>)obj).name());
    }

    public Object toGenericEnum(Class clazz, Enum enum_) {
        return Enum.valueOf(clazz, enum_.name());
    }

    @Override
    public Class<T> getSpecificType() {
        return this.specificType;
    }

    public Object getGeneric(Class clazz, Object obj) {
        return this.toGenericEnum(clazz, (Enum<?>)obj);
    }

    @Override
    public Object getGeneric(T enumVal) {
        return enumVal;
    }

    @Override
    public T getSpecific(Object obj) {
        return this.toSpecificEnum(obj);
    }
}
