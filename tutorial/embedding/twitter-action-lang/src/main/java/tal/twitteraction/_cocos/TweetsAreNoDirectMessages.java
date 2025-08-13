/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package tal.twitteraction._cocos;

import de.se_rwth.commons.logging.Log;
import tal.twitteraction._ast.ASTTweet;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 *
 */
public class TweetsAreNoDirectMessages implements TwitterActionASTTweetCoCo {

	@Override
	public void check(ASTTweet node) {
		String message = node.getMsg().getValue();
		if (message.charAt(0) == '@') {
			Log.error(
					"Tweets beginning with an @ character will be interpreted as direct message instead. Use 'dm <msg>' instead.");
		}

	}

}
