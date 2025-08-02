package net.fawnoculus.nochat.util.config.options;

import net.fawnoculus.nochat.util.config.ConfigFile;
import org.jetbrains.annotations.Nullable;



public class StringOption extends Option<String> {
  
  public StringOption(ConfigFile parent, String name, String defaultValue, @Nullable String comment) {
    super(parent, name, defaultValue, comment);
  }
  
  @Override
  boolean additionalValidation(String value) {
    return switch (this.ExtraType){
      case Option.ExtraType.AllowedValues item -> item.isValid(value);
      case Option.ExtraType.Generic ignored -> true;
      default -> false;
    };
  }
}
