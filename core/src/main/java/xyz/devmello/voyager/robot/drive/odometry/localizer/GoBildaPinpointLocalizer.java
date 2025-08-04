package xyz.devmello.voyager.robot.drive.odometry.localizer;

import android.util.Log;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import xyz.devmello.voyager.math.geometry.PointXYZ;
import xyz.devmello.voyager.robot.AbstractOdometry;
import xyz.devmello.voyager.robot.devices.GoBildaPinpointDriver;
import xyz.devmello.voyager.robot.devices.GoBildaPinpointDriverVoyager;

public class GoBildaPinpointLocalizer  extends AbstractOdometry {

    public static class Params {
        public double xOffset = 0;
        public double yOffset = 0;
        public DistanceUnit offsetUnit = DistanceUnit.MM;
        public GoBildaPinpointDriverVoyager.GoBildaOdometryPods resolution = GoBildaPinpointDriverVoyager.GoBildaOdometryPods.goBILDA_4_BAR_POD;
        public GoBildaPinpointDriverVoyager.EncoderDirection xDirection = GoBildaPinpointDriverVoyager.EncoderDirection.FORWARD;
        public GoBildaPinpointDriverVoyager.EncoderDirection yDirection = GoBildaPinpointDriverVoyager.EncoderDirection.FORWARD;
    }
    public static Params PARAMS = new Params();
    private final GoBildaPinpointDriverVoyager driver;

    public GoBildaPinpointLocalizer(HardwareMap hm, String odo, PointXYZ startPose) {
        driver = hm.get(GoBildaPinpointDriverVoyager.class, odo);
        driver.setOffsets(PARAMS.xOffset, PARAMS.yOffset, PARAMS.offsetUnit);
        driver.setEncoderDirections(PARAMS.xDirection, PARAMS.yDirection);
        driver.setEncoderResolution(PARAMS.resolution);
        Log.d("PinpointDrive", "Initializing Odometry");
        driver.resetPosAndIMU();
        // wait for pinpoint to finish calibrating
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        driver.setPosition(startPose);
    }

    @Override
    public void tick() {
        driver.update();
    }

    @Override
    public PointXYZ getRawPosition() {
        return driver.getPositionXYZ();
    }

    @Override
    public void setOffset(PointXYZ pointXYZ) {
        driver.setPosition(pointXYZ);
    }
}
