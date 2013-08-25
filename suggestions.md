# Possible improvements on Mule ESB build

## Approach taken:

I decided to do some profiling to find possible improvements on build performance, so all my suggestions are based on these profiling checks.

I've used the Maven Extension maven-build-utils (https://github.com/lkoskela/maven-build-utils) or a similar one to check, in a goal level, what goals during the build process takes longer to finish, and then work on it to improve performance:

For example, over the core codebase, I've done the following execution: 
	mvn clean install -Pperfstats -Dduration.output=file -Dduration.output.file=./perfstats.log

Obtaining the following report: 
PROJECT                                                    DURATION
| PHASE                                                       PERCENTAGE
| | GOAL                                                          |    |
| | |                                                             |    |
*mule-core                                                   576.5s 100%
  clean                                                        1.2s   0%
    maven-clean-plugin:clean                                   0.3s  23%
    cobertura-maven-plugin:clean                               0.9s  76%
  validate                                                     1.6s   0%
    maven-enforcer-plugin:enforce                              0.5s  30%
    buildnumber-maven-plugin:create                            1.1s  69%
  process-resources                                            0.2s   0%
    maven-resources-plugin:resources                           0.2s 100%
  compile                                                      9.0s   1%
    maven-compiler-plugin:compile                              9.0s 100%
  generate-test-resources                                      0.1s   0%
    native2ascii-maven-plugin:native2ascii                     0.1s 100%
  process-test-resources                                       0.0s   0%
    maven-resources-plugin:testResources                       0.0s 100%
  test-compile                                                 4.7s   0%
    maven-compiler-plugin:testCompile                          4.7s 100%
  test                                                       556.2s  96%
    maven-surefire-plugin:test                               556.2s 100%
  package                                                      3.3s   0%
    maven-jar-plugin:jar                                       2.4s  71%
    maven-jar-plugin:test-jar                                  1.0s  28%
  install                                                      0.1s   0%
    maven-install-plugin:install                               0.1s 100%

As the part which takes longer is the test, I decided to search on ways to improve test's speed and quality. Also, my improvements are basically automating the process I'm taking to check what can be improved, to turn this process repeatable.

Also, I thought about improving the profiling using one of the following approaches, to take more details:
* http://jira.codehaus.org/browse/MNG-3547
* http://jira.codehaus.org/browse/MNG-4639

## Improvements

### Minor improvements
* common dependencies could be moved to an external pom, because the pom.xml could be simpler and smaller
* fix some links to external websites
* Remove the "all-modules" pom.xml, and automatically generate the pom.xml from the list of modules.
* Remove version tag from modules


### Improvement 1: Include a coverage check

I've seen that Cobertura is currently disabled because of "trouble with the dependency unpack goal in the catalog archetype". 

I suggest to include again a coverage check: 
* to be sure that the code coverage respects a minimum percentage;
* to verify if the high number of tests reflects a good and uniform code coverage. 

Using JaCoCo, return to Clover (check if MCLOVER-50 and other possible problems are solved) or even enable Cobertura again are possible choices.

After running a cobertura:cobertura goal in the core module (to use what already exists), I've checked that 58% of the code is covered by tests. It seems to be a good number, because mostly of the non verified methods are interfaces. 

If the code coverage is not a useful approach, so I suggest to remove cobertura:clean executions. It takes time (not that big, but is almost 1 sec for nothing).

#### Benefits/Motivation

* Despite some discussions on the necessity of the code coverage and the misuse of it, the code coverage can be used to check if the time spent on test execution is consistent with the codebase size and complexity; 
* Organizing the pom.xml, to remove unnecessary and time-spending executions (in case of removing cobertura:clean).

### Improvement 2: Generate a surefire report to check possible bottlenecks

After running the surefire test report I've checked that 9% of the time (~50s) was spent on ~3% of the tests (75/2580), all of them into package org.mule.util.store. That's a possible example of package tested with too much time spending (pending analysis). So I suggest to create a profile for "test profiling analysis", allowing 

#### Benefits/Motivation

* Generating this report can be useful to check what tests (as the example above) are slowing the build running, allowing refactoring of these tests when feasible.
* It enables a quantitative approach to guide the test execution analysis.

### Improvement 3: Break the tests into groups

Using Junit org.junit.experimental.categories.Category resource, I suggest to create categories by speed execution (Slow, Slower, Fast, e.g.), and apply them gradually to the slower tests, allowing a filtering per speed.

#### Benefits/Motivation

* classifying the tests according to the speed would allow filtering the tests pending on refactoring/analysis, as well as not executing them when on daily development (before commit).

### Improvement 4: Run parallel JUnit tests

Run parallel tests could be another possibility to reduce the time spent on testing phase. Combining it with Maven multithreading or using the fork configuration on surefire-report plugin. This can be hazardous, however.

#### Benefits/Motivation

* A faster build execution, enabling a larger number of tests being executed at the same time.

#### REMOVE IT BELOW!
http://maven.apache.org/surefire/maven-surefire-plugin/examples/fork-options-and-parallel-execution.html
