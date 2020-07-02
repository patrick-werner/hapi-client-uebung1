import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

public class Demo {

  public static void main(String[] args) {

    FhirContext ctx = FhirContext.forR4();
    String serverBase = "http://hapi.fhir.org/baseR4";
    IGenericClient client = ctx.newRestfulGenericClient(serverBase);

    //Patient
    Bundle results = client.search().forResource(Patient.class)
        .where(Patient.NAME.matches().value("Smith")).returnBundle(Bundle.class).execute();
    results.getEntry().stream().forEach(p -> System.out.println(p.getId()));


    Patient pat = (Patient) results.getEntryFirstRep().getResource();

    System.out.println("Vorname: " + pat.getNameFirstRep().getGivenAsSingleString());
    System.out.println("==========");
    System.out.println("++++++++++");
    System.out.println("==========");

    System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(pat));
    System.out.println("==========");
    System.out.println("++++++++++");
    System.out.println("==========");

    //Fieber
    Observation obsKoerperTemp = new Observation();
    obsKoerperTemp.setSubject(new Reference(pat).setDisplay("John Doe"));
    obsKoerperTemp.getCategoryFirstRep().getCodingFirstRep()
        .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
        .setCode("vital-signs").setDisplay("Vital Signs");
    obsKoerperTemp.setStatus(ObservationStatus.FINAL);
    obsKoerperTemp.getCode().setText("Fieber (über 38°C)").getCodingFirstRep().setCode("386661006")
        .setSystem("http://snomed.info/sct").setDisplay("Fever (finding)");
    obsKoerperTemp.getValueCodeableConcept().getCodingFirstRep()
        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0136").setCode("Y").setDisplay("Yes");

    System.out
        .println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(obsKoerperTemp));
    System.out.println("==========");
    System.out.println("++++++++++");
    System.out.println("==========");

    //Validierung
    MethodOutcome outcome = client.validate().resource(obsKoerperTemp).execute();
    OperationOutcome oo = (OperationOutcome) outcome.getOperationOutcome();
    oo.getIssue()
        .forEach(i -> System.out.println(i.getSeverity().getDisplay() + ": " + i.getDiagnostics()));

    MethodOutcome createOutcome = client.create().resource(obsKoerperTemp).execute();
    IIdType id = createOutcome.getId();
    System.out.println("==========");
    System.out.println("++++++++++");
    System.out.println("==========");
    System.out.println("Got ID: " + id.getValue());
  }
}