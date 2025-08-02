package net.fawnoculus.nochat.util.config.options;

import net.fawnoculus.nochat.util.config.ConfigFile;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringListOption extends Option<List<String>> {
  public StringListOption(ConfigFile parent, String name, List<String> defaultValue, @Nullable String comment) {
    super(parent, name, defaultValue, comment);
  }
  
  @Override
  boolean additionalValidation(List<String> value) {
    for (String str : value){
      if(!isEntryValid(str)) return false;
    }
    return true;
  }
  
  public boolean isEntryValid(String str) {
    return switch (this.ExtraType){
      case Option.ExtraType.AllowedValues item -> item.isValid(str);
      case Option.ExtraType.Generic ignored -> true;
      default -> false;
    };
  }
}
