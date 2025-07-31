package net.fawnoculus.nochat.util.config.filetype;

import net.fawnoculus.nochat.util.ExceptionUtil;
import net.fawnoculus.nochat.util.config.ConfigFileType;
import net.fawnoculus.nochat.util.config.options.*;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesConfigFile implements ConfigFileType {
  @Override
  public String getFileExtension() {
    return ".properties";
  }
  
  @Override
  public Boolean isValidOption(Option<?> option) {
    return !isStringInvalid(option.NAME);
  }
  
  @Override
  public Boolean isValidValue(Object value) {
    switch (value){
      case String string ->{
        return !isStringInvalid(string);
      }
      case String[] strings ->{
        for(String string: strings){
          if(isStringInvalid(string)) return false;
        }
        return true;
      }
      default -> {
        return true;
      }
    }
  }
  
  private boolean isStringInvalid(String string){
    return string.contains("\n")
        || string.contains("=")
        || string.contains("#");
  }
  
  @Override
  public List<Option<?>> readFile(File configFile, Logger LOGGER, List<Option<?>> expectedOptions){
    Properties properties = new Properties();
    try {
      properties.load(new FileReader(configFile));
    } catch (Exception e){
      LOGGER.error("Failed to parse Properties Config File '{}' \nException: {}", configFile.getPath(), ExceptionUtil.makePretty(e));
      return expectedOptions;
    }
    
    for(Option<?> expectedOption : expectedOptions){
      String readValue = properties.getProperty(expectedOption.NAME);
      if(readValue == null) {
        LOGGER.warn("Didn't find option '{}' of type '{}' in Config File '{}', using default value", expectedOption.NAME, expectedOption.getClass().getName(), configFile.getPath());
        continue;
      }
      
      switch (expectedOption){
        case BooleanOption booleanOption -> booleanOption.setValue(Boolean.parseBoolean(readValue));
        case DoubleOption doubleOption -> {
          try {
            doubleOption.setValue(Double.parseDouble(readValue));
          } catch (Exception exception){
            LOGGER.error("Failed to parse Double Option '{}' in Config File '{}' Exception: {}", doubleOption.NAME ,configFile.getPath(), exception);
          }
        }
        case FloatOption floatOption -> {
          try {
            floatOption.setValue(Float.parseFloat(readValue));
          } catch (Exception exception){
            LOGGER.error("Failed to parse Float Option '{}' in Config File '{}' Exception: {}", floatOption.NAME ,configFile.getPath(), exception);
          }
        }
        case IntegerOption integerOption -> {
          try {
            integerOption.setValue(Integer.parseInt(readValue));
          } catch (Exception exception){
            LOGGER.error("Failed to parse Integer Option '{}' in Config File '{}' Exception: {}", integerOption.NAME ,configFile.getPath(), exception);
          }
        
        }
        case StringOption stringOption -> stringOption.setValue(readValue);
        case StringListOption stringListOption -> {
          char[] Chars = readValue.toCharArray();
          
          List<String> stringList = new ArrayList<>();
          StringBuilder currentString = new StringBuilder();
          for (char currentChar : Chars) {
            if (currentChar == ';' && currentString.isEmpty()) {
              LOGGER.error("Failed to parse String List Option '{}' in Config File '{}': Unexpected ';' after empty String", stringListOption.NAME, configFile.getPath());
              stringList = new ArrayList<>(stringListOption.getDefaultValue());
              break;
            }
            if (currentChar != ';') {
              currentString.append(currentChar);
              continue;
            }
            
            // Only allow valid Entries
            if(stringListOption.isEntryValid(currentString.toString())) {
              stringList.add(currentString.toString());
            }
            currentString = new StringBuilder();
          }
          //Add the Last Element
          if(!currentString.isEmpty()) stringList.add(currentString.toString());
          stringListOption.setValue(stringList);
        }
        default -> {
          LOGGER.error("Tried to read Option with unknown Option Type: {}", expectedOption.getClass());
          LOGGER.error("To whichever Developer did this, don't forget to add write & read methods for your option types to all filetypes when you add option types when you add them!");
        }
      }
    }
    
    return expectedOptions;
  }
  
  @Override
  public void writeFile(File configFile, Logger LOGGER, List<Option<?>> options) throws IOException {
    FileWriter writer = new FileWriter(configFile);
    
    for(Option<?> option : options){
      if(option.COMMENT != null) writer.write(String.format("#%s\n", option.COMMENT));
      
      switch (option){
        case StringListOption stringListOption ->{
          List<String> stringList = stringListOption.getValue();
          
          writer.write(String.format("%s=%s", option.NAME, stringList.getFirst()));
          
          for (int i = 1; i < stringList.size(); i++) {
            writer.write(String.format(";%s", stringList.get(i)));
          }
          
          writer.write("\n");
        }
        case BooleanOption ignored -> writer.write(String.format("%s=%s\n", option.NAME, option.getValue()));
        case DoubleOption ignored -> writer.write(String.format("%s=%s\n", option.NAME, option.getValue()));
        case FloatOption ignored -> writer.write(String.format("%s=%s\n", option.NAME, option.getValue()));
        case IntegerOption ignored -> writer.write(String.format("%s=%s\n", option.NAME, option.getValue()));
        case StringOption ignored -> writer.write(String.format("%s=%s\n", option.NAME, option.getValue()));
        default -> {
          LOGGER.error("Tried to write Option with unknown Option Type: {}", option.getClass());
          LOGGER.error("To whichever Developer did this, don't forget to add write & read methods for your option types to all filetypes when you add option types when you add them!");
        }
      }
    }
    writer.flush();
    writer.close();
  }
}
