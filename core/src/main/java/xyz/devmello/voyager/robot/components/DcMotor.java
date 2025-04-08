package xyz.devmello.voyager.robot.components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import xyz.devmello.voyager.utils.ValidationUtils;

public class DcMotor extends BaseMotor  {
    public final com.qualcomm.robotcore.hardware.DcMotor motor;

    public DcMotor(String name, HardwareMap hardwareMap) {
        this(name, hardwareMap, false);
    }

    public DcMotor(String name, HardwareMap hardwareMap, boolean isInverted) {
        this(name, hardwareMap, isInverted, false);
    }

    public DcMotor(String name, HardwareMap hardwareMap, boolean isSetInverted,
                   boolean isGetInverted) {
        this(name, hardwareMap, isSetInverted, isGetInverted, 0);
    }

    public DcMotor(String name, HardwareMap hardwareMap, boolean isSetInverted,
                   boolean isGetInverted,
                   double deadband) {
        ValidationUtils.validate(deadband, "deadband");
        this.motor = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotor.class, name);
        setIsSetInverted(isSetInverted);
        setIsGetInverted(isGetInverted);
        setDeadband(deadband);
    }


    @Override
    public void abstractSetPower(double power) {
        motor.setPower(power);
    }

    @Override
    public double abstractGetPower() {
        return motor.getPower();
    }

}
