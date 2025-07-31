package net.fawnoculus.nochat.util.config.options;

import net.fawnoculus.nochat.util.config.ConfigFile;
import org.jetbrains.annotations.Nullable;

public class IntegerOption extends Option<Integer> {
  public IntegerOption(ConfigFile parent, String name, Integer defaultValue, @Nullable String comment) {
    super(parent, name, defaultValue, comment);
  }
  
  @Override
  boolean additionalValidation(Integer value) {
    return switch (this.ExtraType){
      case Option.ExtraType.IntRange intRange -> intRange.isValid(value);
      case Option.ExtraType.FloatRange floatRange -> floatRange.isValid(value);
      case Option.ExtraType.Generic ignored -> true;
      default -> false;
    };
  }
}
