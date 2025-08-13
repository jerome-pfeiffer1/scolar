package implication;

import de.monticore.io.paths.MCPath;
import java.nio.file.Path;

public interface ImplicationCalculator {

  /**
   * Calculate the implications for the given component located in the given model path
   * and output the modified component to the output path.
   * @param qualifiedComponentName The name of the language component
   *                               to calculate the implications for
   * @param modelPath The model path containing the component to modify
   * @param outputPath The output path for the modified language component
   */
  void calculateImplications(
      String qualifiedComponentName,
      MCPath modelPath,
      Path outputPath);
}
