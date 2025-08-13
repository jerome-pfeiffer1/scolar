/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package tal.twitteraction._generator;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public interface ITwitterActionGenerator {
	void generateDirectMessageCall(String apiKey, tal.twitteraction._ast.ASTDirectMessage ast);
    void generateTweetCallt(String apiKey, tal.twitteraction._ast.ASTTweet ast);

  Class<?> getTargetInterface();
}
