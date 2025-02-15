/*
 * Copyright (c) 2021.
 *
 * This file is part of the "Pathfinder2" project, available here:
 * <a href="https://github.com/Wobblyyyy/Pathfinder2">GitHub</a>
 *
 * This project is licensed under the GNU GPL V3 license.
 * <a href="https://www.gnu.org/licenses/gpl-3.0.en.html">GNU GPL V3</a>
 */

package xyz.devmello.voyager.control;

import xyz.devmello.voyager.math.MinMax;

/**
 * A proportional, integral, derivative controller. This is like the
 * {@link ProportionalController}'s cool older cousin. Not only does it
 * factor in proportion, it even brings in the integral and derivative
 * components as well! And you thought you'd never use calculus...
 *
 * @author Colin Robertson
 * @since 0.15.0
 */
public class PIDController extends AbstractController {
    private final MiniPID pid;

    public PIDController(double p, double i, double d) {
        this(p, i, d, 0.02);
    }

    public PIDController(double p, double i, double d, double f) {
        pid = new MiniPID(p, i, d, f);
    }

    @Override
    public double calculate(double value) {
        return clip(pid.getOutput(value, getTarget()));
    }

    public PIDController setP(double p) {
        pid.setP(p);

        return this;
    }

    public PIDController setI(double i) {
        pid.setI(i);

        return this;
    }

    public PIDController setD(double d) {
        pid.setDerivative(d);

        return this;
    }

    public PIDController setF(double f) {
        pid.setFeedForward(f);

        return this;
    }

    /**
     * This is a class taken completely from another project, and is not
     * written by me. It is used to calculate the PID values.
     * @see <a href="https://github.com/tekdemo/MiniPID-Java/blob/master/src/com/stormbots/MiniPID.java">...</a>
     */

    static class MiniPID {
        private double proportional;
        private double integral;
        private double derivative;
        private double feedForward;
        private double maxIOutput;
        private double maxError;
        private double errorSum;
        private double maxOutput;
        private double minOutput;
        private double setpoint;
        private double lastActual;
        private boolean firstRun = true;
        private boolean reversed;
        private double outputRampRate;
        private double lastOutput;
        private double outputFilter;
        private double setpointRange;

        public MiniPID(double p, double i, double d) {
            proportional = p;
            integral = i;
            derivative = d;
            checkSigns();
        }

        public MiniPID(double p, double i, double d, double f) {
            proportional = p;
            integral = i;
            derivative = d;
            feedForward = f;
            checkSigns();
        }

        public void setP(double p) {
            proportional = p;
            checkSigns();
        }

        public void setI(double i) {
            if (integral != 0) {
                errorSum = errorSum * integral / i;
            }
            if (maxIOutput != 0) {
                maxError = maxIOutput / i;
            }
            integral = i;
            checkSigns();
        }

        public void setDerivative(double derivative) {
            this.derivative = derivative;
            checkSigns();
        }

        public void setFeedForward(double feedForward) {
            this.feedForward = feedForward;
            checkSigns();
        }

        public void setPID(double p, double i, double d) {
            proportional = p;
            derivative = d;
            setI(i);
            checkSigns();
        }

        public void setPID(double p, double i, double d, double f) {
            proportional = p;
            derivative = d;
            feedForward = f;
            setI(i);
            checkSigns();
        }

        public void setMaxIOutput(double maximum) {
            maxIOutput = maximum;
            if (integral != 0) {
                maxError = maxIOutput / integral;
            }
        }

        public void setOutputLimits(double output) {
            setOutputLimits(-output, output);
        }

        public void setOutputLimits(double minimum, double maximum) {
            if (maximum < minimum) return;
            maxOutput = maximum;
            minOutput = minimum;

            if (maxIOutput == 0 || maxIOutput > (maximum - minimum)) {
                setMaxIOutput(maximum - minimum);
            }
        }

        public void setDirection(boolean reversed) {
            this.reversed = reversed;
        }

        public void setSetpoint(double setpoint) {
            this.setpoint = setpoint;
        }

        public double getOutput(double actual, double setpoint) {
            double output;
            double pOut;
            double iOut;
            double dOut;
            double fOut;
            this.setpoint = setpoint;
            if (setpointRange != 0) {
                setpoint =
                    constrain(
                        setpoint,
                        actual - setpointRange,
                        actual + setpointRange
                    );
            }
            double error = setpoint - actual;
            fOut = feedForward * setpoint;
            pOut = proportional * error;
            if (firstRun) {
                lastActual = actual;
                lastOutput = pOut + fOut;
                firstRun = false;
            }
            dOut = -derivative * (actual - lastActual);
            lastActual = actual;
            iOut = integral * errorSum;
            if (maxIOutput != 0) {
                iOut = constrain(iOut, -maxIOutput, maxIOutput);
            }
            output = fOut + pOut + iOut + dOut;
            if (
                minOutput != maxOutput && !bounded(output, minOutput, maxOutput)
            ) {
                errorSum = error;
            } else if (
                outputRampRate != 0 &&
                !bounded(
                    output,
                    lastOutput - outputRampRate,
                    lastOutput + outputRampRate
                )
            ) {
                errorSum = error;
            } else if (maxIOutput != 0) {
                errorSum = constrain(errorSum + error, -maxError, maxError);
            } else {
                errorSum += error;
            }
            if (outputRampRate != 0) {
                output =
                    constrain(
                        output,
                        lastOutput - outputRampRate,
                        lastOutput + outputRampRate
                    );
            }
            if (minOutput != maxOutput) {
                output = constrain(output, minOutput, maxOutput);
            }
            if (outputFilter != 0) {
                output =
                    lastOutput * outputFilter + output * (1 - outputFilter);
            }
            lastOutput = output;
            return output;
        }

        public double getOutput() {
            return getOutput(lastActual, setpoint);
        }

        public double getOutput(double actual) {
            return getOutput(actual, setpoint);
        }

        public void reset() {
            firstRun = true;
            errorSum = 0;
        }

        public void setOutputRampRate(double rate) {
            outputRampRate = rate;
        }

        public void setSetpointRange(double range) {
            setpointRange = range;
        }

        public void setOutputFilter(double strength) {
            if (strength == 0 || bounded(strength, 0, 1)) {
                outputFilter = strength;
            }
        }

        private double constrain(double value, double min, double max) {
            return MinMax.clip(value, min, max);
        }

        private boolean bounded(double value, double min, double max) {
            return (min < value) && (value < max);
        }

        private void checkSigns() {
            if (reversed) {
                if (proportional > 0) proportional *= -1;
                if (integral > 0) integral *= -1;
                if (derivative > 0) derivative *= -1;
                if (feedForward > 0) feedForward *= -1;
            } else {
                if (proportional < 0) proportional *= -1;
                if (integral < 0) integral *= -1;
                if (derivative < 0) derivative *= -1;
                if (feedForward < 0) feedForward *= -1;
            }
        }
    }
}
