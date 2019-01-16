import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * "copy" the definitions/XXX from yamlFileToMergeIn in yamlBaseFileIn (in case that is not included in the base file)
 */
public class MergeDefnitionsFromYamlToYaml {

    public static void main(String[] args) throws IOException {
        String yamlBaseFileIn = "in_1_swagger.yaml";
        String yamlFileToMergeIn = "in_2_swagger.yaml";
        String yamlFileOut = "out_swagger.yaml";

        System.out.printf("Merging definitions of file '%s' in the definitions of '%s', generating the file '%s'  \n",
                yamlFileToMergeIn, yamlFileToMergeIn, yamlBaseFileIn);

        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yamlFactory);
        ObjectNode rootBaseSource = (ObjectNode) mapper.readTree(new File(yamlBaseFileIn));
        ObjectNode rootToMerge = (ObjectNode) mapper.readTree(new File(yamlFileToMergeIn));

        Iterator<String> definitions = rootToMerge.path("definitions").fieldNames();
        while (definitions.hasNext()) {
            String name = definitions.next();
            JsonNode definitionToMerge = rootToMerge.path("definitions").path(name);

            if (rootBaseSource.path("definitions").path(name).isMissingNode()) {
                System.out.printf(" --> adding name %s \n", name);
                ((ObjectNode) rootBaseSource.path("definitions") ).set(name, definitionToMerge);
            }
        }

        FileOutputStream fos = new FileOutputStream(yamlFileOut);
        SequenceWriter sw = mapper.writerWithDefaultPrettyPrinter().writeValues(fos);
        sw.write(rootBaseSource);

        System.out.printf("Done");
    }
}
