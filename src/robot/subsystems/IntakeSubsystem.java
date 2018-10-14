package robot.subsystems;

import com.torontocodingcollective.sensors.limitSwitch.TLimitSwitch;
import com.torontocodingcollective.speedcontroller.TCanSpeedController;
import com.torontocodingcollective.speedcontroller.TCanSpeedControllerType;
import com.torontocodingcollective.subsystem.TSubsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.RobotMap;
import robot.commands.intake.DefaultIntakeCommand;

public class IntakeSubsystem extends TSubsystem {

	// Limit switches to detect if there is a cube loaded
	public TLimitSwitch leftCubeDetectedSwitch = new TLimitSwitch(RobotMap.LEFT_CUBE_DETECT_DIO_PORT,
			TLimitSwitch.DefaultState.TRUE);
	public TLimitSwitch rightCubeDetectedSwitch = new TLimitSwitch(RobotMap.RIGHT_CUBE_DETECT_DIO_PORT,
			TLimitSwitch.DefaultState.TRUE);

	// Motor that moves the roller to suck in the cube
	private TCanSpeedController intakeRollerMotorLeft = new TCanSpeedController(TCanSpeedControllerType.VICTOR_SPX,
			RobotMap.INTAKE_ROLLER_MOTOR_LEFT_CAN_ADDRESS, true);
	// Motor that moves the arm up and down
	private TCanSpeedController intakeRollerMotorRight = new TCanSpeedController(TCanSpeedControllerType.TALON_SRX,
			RobotMap.INTAKE_ROLLER_MOTOR_RIGHT_CAN_ADDRESS, true);


	// The intake clamp
	private DoubleSolenoid intakeClaw = new DoubleSolenoid(RobotMap.INTAKE_CLAW_PNEUMATIC_PORT, RobotMap.INTAKE_CLAW_PNEUMATIC_PORT2);
	
	// // The motors in the claw (arm) that "sucks" in the cube
	// private TCanSpeedController rightIntakeClawMotor = new
	// TCanSpeedController(TCanSpeedControllerType.TALON_SRX,
	// RobotMap.RIGHT_INTAKE_CLAW_WHEELS_CAN_ADDRESS);
	// private TCanSpeedController leftIntakeClawMotor = new
	// TCanSpeedController(TCanSpeedControllerType.TALON_SRX,
	// RobotMap.LEFT_INTAKE_CLAW_WHEELS_CAN_ADDRESS);
	//
	// // Intake wheels used to draw in or remove a cube (the wheels inside the
	// robot)
	// private TCanSpeedController leftIntakeMotor = new
	// TCanSpeedController(TCanSpeedControllerType.TALON_SRX,
	// RobotMap.LEFT_INTAKE_RAIL_WHEELS_CAN_ADDRESS);
	// private TCanSpeedController rightIntakeMotor = new
	// TCanSpeedController(TCanSpeedControllerType.TALON_SRX,
	// RobotMap.RIGHT_INTAKE_RAIL_WHEELS_CAN_ADDRESS);

	// Encoder count for when the lift motors are at the highest and the lowest
	public final double LIFT_UP_ENCODER_COUNT = 39500;
	public final double LIFT_MID_ENCODER_COUNT = 20750;
	public final double LIFT_DOWN_ENCODER_COUNT = 0;


	@Override
	public void init() {
		intakeClawClose();
	}
	
	public void setTiltAngle(int angle) {
	}

	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new DefaultIntakeCommand());
	}

	public void intakeClawOpen() {
		intakeClaw.set(Value.kForward);
	}

	public void intakeClawClose() {
		intakeClaw.set(Value.kReverse);
	}

	public void intakeCube() {
		intakeRollerMotorLeft.set(1.0);
		intakeRollerMotorRight.set(1.0);
	}
	public void outtakeCube() {
		intakeRollerMotorLeft.set(-0.5);
		intakeRollerMotorRight.set(-0.5);
	}
	public void outtakeCube(double power) {
		intakeRollerMotorLeft.set(-power * 0.8);
		intakeRollerMotorRight.set(-power * 0.8);
	}
	public void outtakeCubeOP(double power) {
		intakeRollerMotorLeft.set(-power);
		intakeRollerMotorRight.set(-power);
	}
	

	public boolean isCubeDetected() {
		// Cube is detected if both limit switches have detected something, aka a cube
		return leftCubeDetectedSwitch.atLimit() || rightCubeDetectedSwitch.atLimit();
	}


	public void intakeStop() {
		intakeRollerMotorLeft.stopMotor();
		intakeRollerMotorRight.stopMotor();
	}
	
	public boolean isIntakeMotorRunning() {
		return intakeRollerMotorLeft.get() > 0.1;
	}

	// Periodically update the dashboard and any PIDs or sensors
	@Override
	public void updatePeriodic() {
		SmartDashboard.putBoolean("Intake Claw Open", intakeClaw.get() == Value.kForward);
		SmartDashboard.putBoolean("Intake Cube Detected", isCubeDetected());
	}

}
