package net.fawnoculus.nochat.util.config;


import net.fabricmc.loader.api.FabricLoader;
import net.fawnoculus.nochat.util.ExceptionUtil;
import net.fawnoculus.nochat.util.config.options.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigFile {
  public final Logger LOGGER;
  public final File CONFIG_FILE;
  public final ConfigFileType CONFIG_FILE_TYPE;
  public final List<String> OPTION_NAMES = new ArrayList<>();
  public List<Option<?>> options = new ArrayList<>();
  public boolean initialized = false;
  
  
  
  public ConfigFile(String subPath, ConfigFileType configFileType, @Nullable Logger logger){
    this(FabricLoader.getInstance().getConfigDir().resolve(subPath).toAbsolutePath(), configFileType, logger);
  }
  
  public ConfigFile(Path path, ConfigFileType configFileType, @Nullable Logger logger) {
    this.CONFIG_FILE_TYPE = configFileType;
    this.LOGGER = logger != null ? logger : LoggerFactory.getLogger("FawnoculusConfigUtil");
    
    if (path.endsWith(configFileType.getFileExtension())) {
      this.CONFIG_FILE = new File(path.toUri());
    } else {
      this.CONFIG_FILE = new File(path + configFileType.getFileExtension());
    }
  }
  public void readFile() {
    List<Option<?>> readOptions = this.options;
    
    try {
      readOptions = this.CONFIG_FILE_TYPE.readFile(CONFIG_FILE, this.LOGGER, this.options);
    } catch (Exception e) {
      LOGGER.error("Failed to read from Config File: {} \n Exception: {}", CONFIG_FILE.getPath(), ExceptionUtil.makePretty(e));
    }
    
    this.options = readOptions;
  }
  
  public void writeFile() {
    try {
      if (!CONFIG_FILE.exists()) {
        boolean ignored = CONFIG_FILE.getParentFile().mkdirs();
        boolean newFile = CONFIG_FILE.createNewFile();
        if (newFile) {
          LOGGER.info("Created new Config File: {}", CONFIG_FILE.getPath());
        } else {
          LOGGER.warn("Failed to create Config File: {}", CONFIG_FILE.getPath());
        }
      } else {
        boolean delete = CONFIG_FILE.delete();
        if (!delete) {
          LOGGER.warn("Failed to delete old Config File: {}", CONFIG_FILE.getPath());
        }
      }
      
      this.CONFIG_FILE_TYPE.writeFile(CONFIG_FILE, this.LOGGER, this.options);
    } catch (Exception e) {
      LOGGER.error("Failed to write to Config File: {} \n Exception: {}", CONFIG_FILE.getPath(), ExceptionUtil.makePretty(e));
    }
  }
  
  public void initialize() {
    if (initialized) {
      throw new IllegalStateException("Config File already Initialized");
    }
    if(CONFIG_FILE.exists()) readFile();
    writeFile();
    
    initialized = true;
  }
  
  public List<Option<?>> getAllOptions(){
    return List.copyOf(options);
  }
  
  // Everything bellow is just for getting new Options from a Config File //
  
  protected void addAndValidateOption(Option<?> option) {
    String name = option.NAME;
    
    if (!CONFIG_FILE_TYPE.isValidOption(option))
      throw new IllegalArgumentException("Option with name '" + name + "' is not valid for Config Type '" + CONFIG_FILE_TYPE.getClass().getName() + "'");
    if (OPTION_NAMES.contains(name))
      throw new IllegalArgumentException("Option with name '" + name + "' already exist in Config File '" + CONFIG_FILE.getPath() + "'");
    
    this.OPTION_NAMES.add(name);
    this.options.add(option);
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public BooleanOption newBooleanOption(String name, Boolean defaultValue, @Nullable String comment, Option.ExtraType extraType) {
    if(comment != null) comment += " [Default: " + defaultValue + "]";
    
    BooleanOption option = new BooleanOption(this, name, defaultValue, comment);
    option.setExtraType(extraType);
    addAndValidateOption(option);
    return option;
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public BooleanOption newBooleanOption(String name, Boolean defaultValue, @Nullable String comment) {
    return newBooleanOption(name, defaultValue, comment, new Option.ExtraType.Generic());
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public DoubleOption newDoubleOption(String name, Double defaultValue, @Nullable String comment, Option.ExtraType extraType) {
    if (comment != null) comment += " [Default: " + defaultValue + "]";
    
    DoubleOption option = new DoubleOption(this, name, defaultValue, comment);
    option.setExtraType(extraType);
    addAndValidateOption(option);
    return option;
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   * @param min          Minimum allowed Value
   * @param max          Maximum allowed Value
   */
  public DoubleOption newDoubleOption(String name, Double defaultValue, @Nullable String comment, Double min, Double max) {
    if (comment != null) comment += "[min: "+ min +"; max: "+ max +"]";
    
    return newDoubleOption(name, defaultValue, comment, new Option.ExtraType.FloatRange(min, max));
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public DoubleOption newDoubleOption(String name, Double defaultValue, @Nullable String comment) {
    return newDoubleOption(name, defaultValue, comment, new Option.ExtraType.Generic());
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public FloatOption newFloatOption(String name, Float defaultValue, @Nullable String comment, Option.ExtraType extraType) {
    if (comment != null) comment += " [Default: " + defaultValue + "]";
    
    FloatOption option = new FloatOption(this, name, defaultValue, comment);
    option.setExtraType(extraType);
    addAndValidateOption(option);
    return option;
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   * @param min          Minimum allowed Value
   * @param max          Maximum allowed Value
   */
  public FloatOption newFloatOption(String name, Float defaultValue, @Nullable String comment, Float min, Float max) {
    if (comment != null) comment += "[min: "+ min +"; max: "+ max +"]";
    
    return newFloatOption(name, defaultValue, comment, new Option.ExtraType.FloatRange(min, max));
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public FloatOption newFloatOption(String name, Float defaultValue, @Nullable String comment) {
    return newFloatOption(name, defaultValue, comment, new Option.ExtraType.Generic());
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public IntegerOption newIntegerOption(String name, Integer defaultValue, @Nullable String comment, Option.ExtraType extraType) {
    if (comment != null) comment += " [Default: " + defaultValue + "]";
    
    IntegerOption option = new IntegerOption(this, name, defaultValue, comment);
    option.setExtraType(extraType);
    addAndValidateOption(option);
    return option;
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   * @param min          Minimum allowed Value
   * @param max          Maximum allowed Value
   */
  public IntegerOption newIntegerOption(String name, Integer defaultValue, @Nullable String comment, Integer min, Integer max) {
    if (comment != null) comment += "[min: "+ min +"; max: "+ max +"]";
    
    return newIntegerOption(name, defaultValue, comment, new Option.ExtraType.IntRange(min, max));
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public IntegerOption newIntegerOption(String name, Integer defaultValue, @Nullable String comment) {
    return newIntegerOption(name, defaultValue, comment, new Option.ExtraType.Generic());
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public StringOption newStringOption(String name, String defaultValue, @Nullable String comment, Option.ExtraType extraType) {
    if (comment != null) comment += " [Default: " + defaultValue + "]";
    
    StringOption option = new StringOption(this, name, defaultValue, comment);
    option.setExtraType(extraType);
    addAndValidateOption(option);
    return option;
  }
  
  /**
   * @param name          Name of the Option
   * @param defaultValue  the Value that the Option will default to
   * @param comment       Optional Comment (use "null" for no Comment)
   * @param allowedValues All Allowed Values
   */
  public StringOption newStringOption(String name, String defaultValue, @Nullable String comment, String... allowedValues) {
    if (comment != null) {
      StringBuilder commentBuilder = new StringBuilder(comment);
      commentBuilder.append(" ");
      for (int i = 0; i < allowedValues.length - 1; i++) {
        commentBuilder.append(allowedValues[i]).append(",");
      }
      comment = commentBuilder.toString();
      comment += allowedValues[allowedValues.length - 1];
      comment += "]";
    }
    
    return newStringOption(name, defaultValue, comment,  new Option.ExtraType.AllowedValues(allowedValues));
  }
  
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public StringOption newStringOption(String name, String defaultValue, @Nullable String comment) {
    return newStringOption(name, defaultValue, comment, new Option.ExtraType.Generic());
  }
  /**
   * @param name Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment Optional Comment (use "null" for no Comment)
   */
  public StringListOption newStringListOption(String name, List<String> defaultValue, @Nullable String comment, Option.ExtraType extraType) {
    if (comment != null) comment += " [Default: " + defaultValue + "]";
    
    StringListOption option = new StringListOption(this, name, defaultValue, comment);
    option.setExtraType(extraType);
    addAndValidateOption(option);
    return option;
  }
  /**
   * @param name         Name of the Option
   * @param defaultValue the Value that the Option will default to
   * @param comment      Optional Comment (use "null" for no Comment)
   */
  public StringListOption newStringListOption(String name, List<String> defaultValue, @Nullable String comment) {
    return newStringListOption(name, defaultValue, comment, new Option.ExtraType.Generic());
  }
}
