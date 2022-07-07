
# Simple Final State Machine

Each instance of State Machine must be prepared with:

1. Initial state
2. Finish state - may be not several states
3. Intermediate states - optionally
4. All transitions needed

Each State may be specified with handlers:

1. Before handler - is called right before FSM changes INTO this state
2. After handler - is called right before FSM changes FROM this state to another
3. Processor - the method to process events

Transition is the Rule providing FSM the possibility to change between states.

Each transition must be determined in terms of:

1. From State - mandatory
2. To State - mandatory
3. Condition - optionally. If specified, the FSM will check the condition in order to check the possibility
to change from FROM State into TO State

## Example

Simple way to use it - to construct an inherited class specified with the type of events to be processed
 during transitions.

```java
  SimpleFsm<String> simpleFsm = SimpleFsm
    .<SimpleFsm<String>, String>withStates(SimpleFsm::new)
      .from("init")
      .withBeforeHandler(fsm -> initBefore.set(true))
      .withAfterHandler(fsm -> initAfter.set(true))
      .withProcessor((fsm, event) -> initProcess.set(true))
    .end()
    .finish("finish")
      .withBeforeHandler(fsm -> finishBefore.set(true))
      .withAfterHandler(fsm -> finishAfter.set(true))
      .withProcessor((fsm, event) -> finishProcess.set(true))
    .end()
    .withTransition()
      .from("init")
      .to("finish")
      .checking((fsm, event) -> true)
    .end()
    .create();
```

## Releasing

Creating a new release involves the following steps:

1. `./mvnw gitflow:release-start gitflow:release-finish`
2. `git push origin master`
3. `git push --tags`
4. `git push origin develop`

In order to deploy the release to Maven Central, you need to create an account at https://issues.sonatype.org and
configure your account in `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>your-jira-id</username>
      <password>your-jira-pwd</password>
    </server>
  </servers>
</settings>
```

The account also needs access to the project on Maven Central. This can be requested by another project member.

Then check out the release you want to deploy (`git checkout x.y.z`) and run `./mvnw deploy -Prelease`.

## Author

(c) bvn13