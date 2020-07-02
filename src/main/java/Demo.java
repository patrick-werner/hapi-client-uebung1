import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

public class Demo {

  public static void main(String[] args) {

    FhirContext ctx = FhirContext.forR4();
    String serverBase = "https://fhir.molit.eu/fhir";
    IGenericClient client = ctx.newRestfulGenericClient(serverBase);

    //search for Patient
    String name = "Smith";
    Bundle results = client.search().forResource(Patient.class)
        .where(Patient.NAME.matches().value(
            name)).returnBundle(Bundle.class).execute();
    if (results.getEntry().isEmpty()) {
      System.out.println("no Patient with name: " + name + " found");
    }
    results.getEntry().stream().forEach(ec -> System.out.println(ec.getResource().getId()));

    // create Patient
    Patient patient = new Patient();

    patient.addName().setFamily("WernerTest").addGiven("Patrick");
    String s = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
    System.out.println(s);

    MethodOutcome outcome = client
        .create()
        .resource(patient)
        .execute();

    // Print the ID of the newly created resource
    IIdType patId = outcome.getId();
    patient.setId(patId);
    System.out.println("PatientenID: " + patId);

    //UPDATE
    patient.getNameFirstRep().addGiven("noch einer");

    outcome = client
        .update()
        .resource(patient)
        .execute();

    //Delete
    outcome = client
        .delete()
        .resource(patient)
        .execute();
  }
}