/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package tal.twitteraction._generator;

import tal.twitteraction._ast.ASTDirectMessage;
import tal.twitteraction._ast.ASTTweet;

public class TwitterActionGenerator implements ITwitterActionGenerator {

	/**
	 * @see tal.twitteraction._generator.ITwitterActionGenerator#getTargetInterface()
	 */
	@Override
	public Class<?> getTargetInterface() {
		return ITwitterApiCall.class;
	}

	@Override
	public void generateDirectMessageCall(String apiKey, ASTDirectMessage ast) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateTweetCallt(String apiKey, ASTTweet ast) {
		// TODO Auto-generated method stub

	}
}
