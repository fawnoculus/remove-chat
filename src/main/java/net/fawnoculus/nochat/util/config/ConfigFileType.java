package net.fawnoculus.nochat.util.config;


import net.fawnoculus.nochat.util.config.options.Option;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ConfigFileType {
  
  String getFileExtension();
  
  Boolean isValidOption(Option<?> option);
  Boolean isValidValue(Object value);
  
  List<Option<?>> readFile(File configFile, Logger LOGGER, List<Option<?>> expectedOptions);
  void writeFile(File configFile, Logger LOGGER, List<Option<?>> options) throws IOException;
}
