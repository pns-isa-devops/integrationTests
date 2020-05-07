package arquillian;

import fr.unice.polytech.isa.dd.*;
import fr.unice.polytech.isa.dd.entities.*;
import fr.unice.polytech.isa.dd.entities.Package;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import utils.MyDate;

public abstract class AbstractDroneDeliveryTest {

    @Deployment
    public static WebArchive createDeployement(){
        return ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml")

                .addPackage(NextDeliveryInterface.class.getPackage())
                .addPackage(DeliveryInterface.class.getPackage())
                .addPackage(DeliverySchedule.class.getPackage())

                .addPackage(PackageRegistration.class.getPackage())
                .addPackage(PackageFinder.class.getPackage())

                .addPackage(ProviderRegistration.class.getPackage())
                .addPackage(ProviderFinder.class.getPackage())

                .addPackage(CustomerRegistration.class.getPackage())
                .addPackage(CustomerFinder.class.getPackage())

                .addPackage(DeliveryRegistration.class.getPackage())
                .addPackage(AvailableSlotTime.class.getPackage())

                .addPackage(BillingGeneratedInterface.class.getPackage())

                .addPackage(Delivery.class.getPackage())
                .addPackage(Drone.class.getPackage())
                .addPackage(Package.class.getPackage())
                .addPackage(MyDate.class.getPackage())
                .addPackage(Bill.class.getPackage())
                .addPackage(Provider.class.getPackage())
                .addPackage(Customer.class.getPackage())

                .addAsManifestResource(new ClassLoaderAsset("META-INF/persistence.xml"), "persistence.xml");

    }

}
