package mchorse.bbs_mod.utils.keyframes;

import mchorse.bbs_mod.utils.math.IInterpolation;
import mchorse.bbs_mod.utils.math.Interpolation;
import mchorse.bbs_mod.utils.math.Interpolations;
import mchorse.bbs_mod.utils.math.MathUtils;
import org.lwjgl.glfw.GLFW;

public enum KeyframeInterpolation
{
    CONST()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            return KeyframeInterpolations.CONSTANT;
        }
    },
    LINEAR()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            return Interpolation.LINEAR;
        }
    },
    QUAD()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.QUAD_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.QUAD_OUT;

            return Interpolation.QUAD_INOUT;
        }
    },
    CUBIC()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.CUBIC_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.CUBIC_OUT;

            return Interpolation.CUBIC_INOUT;
        }
    },
    HERMITE()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            return KeyframeInterpolations.HERMITE;
        }

        @Override
        public double interpolate(Keyframe a, Keyframe b, double x)
        {
            double v0 = a.prev.getValue();
            double v1 = a.getValue();
            double v2 = b.getValue();
            double v3 = b.next.getValue();

            return Interpolations.cubicHermite(v0, v1, v2, v3, x);
        }
    },
    EXP()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.EXP_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.EXP_OUT;

            return Interpolation.EXP_INOUT;
        }
    },
    BEZIER()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            return KeyframeInterpolations.BEZIER;
        }

        @Override
        public double interpolate(Keyframe a, Keyframe b, double x)
        {
            if (x <= 0) return a.getValue();
            if (x >= 1) return b.getValue();

            /* Transform input to 0..1 */
            double w = b.getTick() - a.getTick();
            double h = b.getValue() - a.getValue();

            /* In case if there is no slope whatsoever */
            if (h == 0) h = 0.00001;

            double x1 = a.getRx() / w;
            double y1 = a.getRy() / h;
            double x2 = (w - b.getLx()) / w;
            double y2 = (h + b.getLy()) / h;
            double e = 0.0005;

            e = h == 0 ? e : Math.max(Math.min(e, 1 / h * e), 0.00001);
            x1 = MathUtils.clamp(x1, 0, 1);
            x2 = MathUtils.clamp(x2, 0, 1);

            return Interpolations.bezier(0, y1, y2, 1, Interpolations.bezierX(x1, x2, x, e)) * h + a.getValue();
        }
    },
    BACK()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.BACK_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.BACK_OUT;

            return Interpolation.BACK_INOUT;
        }
    },
    ELASTIC()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.ELASTIC_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.ELASTIC_OUT;

            return Interpolation.ELASTIC_INOUT;
        }
    },
    BOUNCE()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.BOUNCE_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.BOUNCE_OUT;

            return Interpolation.BOUNCE_INOUT;
        }
    },
    SINE()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.SINE_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.SINE_OUT;

            return Interpolation.SINE_INOUT;
        }
    },
    QUART()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.QUART_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.QUART_OUT;

            return Interpolation.QUART_INOUT;
        }
    },
    QUINT()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.QUINT_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.QUINT_OUT;

            return Interpolation.QUINT_INOUT;
        }
    },
    CIRCLE()
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.CIRCLE_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.CIRCLE_OUT;

            return Interpolation.CIRCLE_INOUT;
        }
    };

    public IInterpolation from(KeyframeEasing easing)
    {
        return null;
    }

    public double interpolate(Keyframe a, Keyframe b, double x)
    {
        IInterpolation interpolation = this.from(a.getEasing());

        return interpolation == null ? a.getValue() : interpolation.interpolate(a.getValue(), b.getValue(), x);
    }

    public boolean isBezier()
    {
        return this == BEZIER;
    }
}
