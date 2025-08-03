package xyz.devmello.voyager.robot.components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import xyz.devmello.voyager.utils.ValidationUtils;

public class DcMotorEx extends BaseMotor{
    private final com.qualcomm.robotcore.hardware.DcMotorEx motor;

    public DcMotorEx(String name, HardwareMap hardwareMap) {
        this(name, hardwareMap, false);
    }

    public DcMotorEx(String name, HardwareMap hardwareMap, boolean isInverted) {
        this(name, hardwareMap, isInverted, false);
    }

    public DcMotorEx(String name, HardwareMap hardwareMap, boolean isSetInverted,
                   boolean isGetInverted) {
        this(name, hardwareMap, isSetInverted, isGetInverted, 0);
    }

    public DcMotorEx(String name, HardwareMap hardwareMap, boolean isSetInverted,
                   boolean isGetInverted,
                   double deadband) {
        ValidationUtils.validate(deadband, "deadband");
        this.motor = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotorEx.class, name);
        setIsSetInverted(isSetInverted);
        setIsGetInverted(isGetInverted);
        setDeadband(deadband);
    }

    public com.qualcomm.robotcore.hardware.DcMotorEx getDcMotorEx() {
        return motor;
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
