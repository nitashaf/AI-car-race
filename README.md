# AI-car-race
In this project, I implemented a reinforcement learning algorithm applied to the classic racetrack problem. The objective was to control a race car on a pre-defined track, aiming to complete the track in the shortest time possible. Unlike a competitive race, this project focuses on a time trial scenario where the car is the only one on the track.

**Problem Overview:**
At each timestep, the car's state is represented by location and velocity, The control variables for the car are acceleration components which determine the car's acceleration at each timestep. These control variables can take values from the set {−1,0,1}, allowing the car to accelerate, decelerate, or maintain its current speed in the x and y directions.
There are some limitations:
Velocity is constrained within the range [−5,5]. Any attempt to accelerate beyond these limits is ignored.
The car's acceleration can fail with a 20% chance. If the acceleration fails, the car's velocity remains unchanged for that timestep.
**Model and Approach:**
To make the problem more realistic, I added stochasticity into the acceleration process. Each acceleration command has an 80% chance of being successfully applied, while the remaining 20% corresponds to a failed acceleration attempt.
For the car's interaction with the racetrack, the key challenge is staying on the track while avoiding collisions with the walls. If the car crashes:
In the first variant, the car is placed at the nearest valid position on the track, and its velocity is reset to zero.
In the second, harsher variant, a crash causes the car to reset to the starting position with its velocity set to zero, effectively requiring the agent to restart from the beginning.
**Implementation Details:**
I implemented both crash variants, allowing for side-by-side experimentation to observe how each variant impacts the agent’s learning process. The project also involved optimizing the agent's learning algorithm, ensuring efficient exploration and exploitation of the racetrack environment.
For testing, I used a variety of tracks, including the R-shaped track (from R-track.txt), where I compared the effects of each crash scenario. For the other tracks, I opted for the first variant where the car is placed at the crash location but is not reset to the starting point.
Through this project, I was able to explore reinforcement learning in a controlled environment, examining key aspects like reward shaping, policy development, and the effect of randomness on decision-making.
