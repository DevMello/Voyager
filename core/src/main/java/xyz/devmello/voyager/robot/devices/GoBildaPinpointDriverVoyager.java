package xyz.devmello.voyager.robot.devices;


import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import xyz.devmello.voyager.math.geometry.PointXY;
import xyz.devmello.voyager.math.geometry.PointXYZ;

@I2cDeviceType
@DeviceProperties(
        name = "goBILDA® Pinpoint Odometry Computer Roadrunner Driver",
        xmlTag = "goBILDAPinpointRR",
        description = "goBILDA® Pinpoint Odometry Computer (IMU Sensor Fusion for 2 Wheel Odometry)"
)
public class GoBildaPinpointDriverVoyager extends GoBildaPinpointDriver {
    public float currentTicksPerMM = 0.0F;

    public GoBildaPinpointDriverVoyager(I2cDeviceSynchSimple deviceClient, boolean deviceClientIsOwned) {
        super(deviceClient, deviceClientIsOwned);
    }

    public void setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods pods) {
        super.setEncoderResolution(pods);
        if (pods == GoBildaOdometryPods.goBILDA_SWINGARM_POD) {
            this.currentTicksPerMM = 13.262912F;
        }

        if (pods == GoBildaOdometryPods.goBILDA_4_BAR_POD) {
            this.currentTicksPerMM = 19.894367F;
        }

    }

    public PointXYZ getPositionXYZ() {
        Pose2D ftcPose = this.getPosition();
        return new PointXYZ(ftcPose.getX(DistanceUnit.INCH), ftcPose.getY(DistanceUnit.INCH), ftcPose.getHeading(AngleUnit.RADIANS));
    }

    public PointXYZ setPosition(PointXYZ pos) {
        this.setPosition(new Pose2D(DistanceUnit.INCH, pos.x(), pos.y(), AngleUnit.RADIANS, pos.z().rad()));
        return pos;
    }

    public PointXYZ getVelocityXYZ() {
        double xVel = this.getVelX(DistanceUnit.INCH);
        double yVel = this.getVelY(DistanceUnit.INCH);
        double headingVel = this.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS);
        return new PointXYZ(
                xVel,
                yVel,
                headingVel
        );
    }
}