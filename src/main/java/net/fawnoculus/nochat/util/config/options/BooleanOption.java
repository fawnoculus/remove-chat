package net.fawnoculus.nochat.util.config.options;

import net.fawnoculus.nochat.util.config.ConfigFile;
import org.jetbrains.annotations.Nullable;

public class BooleanOption extends Option<Boolean> {
  public BooleanOption(ConfigFile parent, String name, Boolean defaultValue, @Nullable String comment) {
    super(parent, name,defaultValue, comment);
  }
  
  @Override
  boolean additionalValidation(Boolean value) {
    return this.ExtraType instanceof Option.ExtraType.Generic;
  }
}
