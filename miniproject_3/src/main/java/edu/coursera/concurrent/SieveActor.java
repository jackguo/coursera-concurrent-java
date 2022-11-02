package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determine the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        if (limit < 2) {
            return 0;
        }

        AtomicInteger count = new AtomicInteger(1);
        SieveActorActor first = new SieveActorActor(2);
        finish(() -> {
            for (int i = 3; i <= limit; i += 2) {
                first.send(new Message(count, i));
            }
        });

        return count.get();
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {

        SieveActorActor next;
        int myPrime;

        public SieveActorActor(int prime) {
            this.myPrime = prime;
        }
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            if (msg instanceof Message) {
                Message m = (Message) msg;
                if (m.numberToCheck > myPrime
                        && m.numberToCheck % myPrime != 0) {
                    if (next == null) {
                        next = new SieveActorActor(m.numberToCheck);
                        m.count.incrementAndGet();
                    }
                    next.send(msg);
                }

            } else {
                throw new IllegalArgumentException("msg should be of type Message");
            }
        }
    }

    private static class Message {
        AtomicInteger count;
        int numberToCheck;
        Message(AtomicInteger c, int n) {
            count = c;
            numberToCheck = n;
        }
    }
}
