package mchorse.bbs_mod;

import mchorse.bbs_mod.entity.ActorEntity;
import mchorse.bbs_mod.film.replays.Replay;
import net.minecraft.entity.LivingEntity;

public class Recording
{
    public Replay replay;
    public State state = State.IDLE;
    public int tick;
    public LivingEntity actor;

    public void play(ActorEntity closestActor)
    {
        this.actor = closestActor;
        this.state = State.PLAYBACK;
        this.tick = 0;
    }

    public void record(LivingEntity player)
    {
        reset(new Replay("..."));

        this.actor = player;
        this.state = State.RECORDING;
    }

    public void reset(Replay newReplay)
    {
        this.replay = newReplay;
        this.state = State.IDLE;
        this.tick = 0;
        this.actor = null;
    }

    public void stop()
    {
        this.state = State.IDLE;
        this.tick = 0;
    }

    public void serverTick()
    {
        if (this.state.isRecording())
        {
            this.replay.keyframes.record(this.tick, this.actor, null);

            this.tick += 1;
        }
        else if (this.state.isPlayback())
        {
            this.replay.keyframes.apply(this.tick, this.actor, null);

            this.tick += 1;

            if (this.tick >= 100)
            {
                this.state = State.IDLE;
            }
        }
    }

    public void clientTick()
    {
        if (this.state.isRecording())
        {
            this.replay.keyframes.record(this.tick, this.actor, null);

            this.tick += 1;
        }
        else if (this.state == State.PLAYBACK)
        {
            this.replay.keyframes.apply(this.tick, this.actor, null);

            this.tick += 1;
        }
    }

    public static enum State
    {
        RECORDING, PLAYBACK, IDLE;

        public boolean isRecording()
        {
            return this == RECORDING;
        }

        public boolean isPlayback()
        {
            return this == PLAYBACK;
        }

        public boolean isIdle()
        {
            return this == IDLE;
        }
    }
}