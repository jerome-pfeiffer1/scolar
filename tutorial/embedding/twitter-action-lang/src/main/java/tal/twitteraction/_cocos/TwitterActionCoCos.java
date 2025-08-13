/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package tal.twitteraction._cocos;

public class TwitterActionCoCos {

  public TwitterActionCoCoChecker createTimeCorrectnessChecks() {
	  TwitterActionCoCoChecker checker = new TwitterActionCoCoChecker();

    checker.addCoCo(new TweetsAreNoDirectMessages());

    return checker;
  }
}
