package me.earth.phobot.modules;

import java.util.function.Consumer;

public class Setting<T> {
    private final String name;
    private T value;
    private final T defaultValue;
    private final T min;
    private final T max;
    private Consumer<T> onChange;
    
    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;
        this.min = null;
        this.max = null;
    }
    
    public Setting(String name, T value, T min, T max) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;
        this.min = min;
        this.max = max;
    }
    
    public void setValue(T value) {
        if (min != null && max != null) {
            if (((Comparable) value).compareTo(min) < 0) {
                value = min;
            }
            if (((Comparable) value).compareTo(max) > 0) {
                value = max;
            }
        }
        this.value = value;
        if (onChange != null) {
            onChange.accept(value);
        }
    }
    
    public T getValue() { return value; }
    public String getName() { return name; }
    public T getDefaultValue() { return defaultValue; }
    public T getMin() { return min; }
    public T getMax() { return getMax(); }
    
    public void setOnChange(Consumer<T> onChange) {
        this.onChange = onChange;
    }
}
