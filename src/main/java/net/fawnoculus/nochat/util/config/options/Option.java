package net.fawnoculus.nochat.util.config.options;

import net.fawnoculus.nochat.util.config.ConfigFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Option<T> {
  public final String NAME;
  public final String COMMENT;
  
  private final ConfigFile PARENT;
  private final T DEFAULT_VALUE;
  
  public Option.ExtraType ExtraType = new ExtraType.Generic();
  private T default_overwrite;
  private   T value;
  
  /**
   * @param name Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment Optional Comment (use "null" for no Comment)
   */
  public Option(@NotNull ConfigFile parent, @NotNull  String name, @NotNull  T defaultValue, @Nullable String comment) {
    this.PARENT = parent;
    this.NAME = name;
    this.DEFAULT_VALUE = defaultValue;
    this.COMMENT = comment;
    
    if(isInvalidValue(defaultValue)){
      throw new IllegalArgumentException("Default Value is not a valid for this Option");
    }
  }
  
  /**
   * @return the current value of the option
   */
  public T getValue() {
    return this.value != null ? this.value : this.getDefaultValue();
  }
  
  /**
   * @return the default value of an option, note that default overwrite applies here
   */
  public T getDefaultValue() {
    return this.default_overwrite != null ? this.default_overwrite : this.DEFAULT_VALUE;
  }
  
  /**
   * sets the value of an option
   * @param value the new value of the option
   */
  public void setValue(@NotNull T value) {
    if(isInvalidValue(value)){
      return;
    }
    this.value = value;
  }
  
  /**
   * Checks of a value is valid for this option
   * @param value the value to be checked
   * @return false if the value is valid, true if it is not
   */
  public boolean isInvalidValue(@NotNull T value) {
    assert PARENT != null;
    if(!additionalValidation(value)) return true;
    return !PARENT.CONFIG_FILE_TYPE.isValidValue(value);
  }
  
  abstract boolean additionalValidation(T value);
  
  /**
   * @param type The Extra Type to be assigned
   */
  public void setExtraType(@NotNull ExtraType type){
    this.ExtraType = type;
    
    if(isInvalidValue(this.getDefaultValue())){
      throw new IllegalArgumentException("Default Value is not a valid for this ExtraType");
    }
  }
  
  /**
   * resets the options value to the default value
   */
  public void resetToDefault(){
    this.setValue(this.getDefaultValue());
  }
  
  /**
   * Overwrites the default option of an option
   * @param value new Default Value, disables default overwrite if value is null
   */
  public void overwriteDefault(@NotNull T value){
    this.default_overwrite = value;
    
    if(isInvalidValue(this.getDefaultValue())){
      throw new IllegalArgumentException("New Default Value is not a valid for this Option");
    }
  }
  
  // Why Java, just why are like this, why can't you understand that both options have the same generic Type
  // But then you let pass in an object without even checking its type or casting it
  // What is wrong with you?
  public void castAndOverwriteDefault(Object object){
    if (object == null) {
      return;
    }
    this.overwriteDefault(value);
  }
  
  // Types for additional Validation
  public interface ExtraType{
    record Generic() implements Option.ExtraType{
    }
    record FloatRange(double min, double max) implements Option.ExtraType{
      public boolean isValid(double value){
        return value > this.min && value < this.max;
      }
    }
    record IntRange(int min, int max) implements Option.ExtraType{
      public boolean isValid(int value){
        return value > this.min && value < this.max;
      }
      public boolean isValid(double value){
        return value > this.min && value < this.max;
      }
    }
    record AllowedValues(String... allowedValues) implements Option.ExtraType{
      public boolean isValid(String string){
        return List.of(allowedValues).contains(string);
      }
    }
  }
}
