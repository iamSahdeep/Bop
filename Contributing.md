# Bop music player contributing

# Contributor's Guideline.

## Introduction

Thank you for your interest in contributing to Bop music player. I appreciate all help with finding and fixing bugs, making performance improvements, and other tasks. Every contribution is helpful and I thank you for your effort. To ensure the process of contributing is as smooth as possible, here are a few guidelines for you to follow.

---

## Feature requests. 

Feature requests are a way for you to pitch your amazing ideas of enhancements or new features you want to see on the app. Your ideas would certainly help to improve the app. However, it is important to browse through the [issue tracker](https://github.com/iamSahdeep/Bop/issues) to see if the feature you want to request for hasn't already been requested by another user. If you have gone through the [issue tracker](https://github.com/iamSahdeep/Bop/issues) and the feature request hasn't been made, feel free to request the new feature. To do that, you need to open an [issue](https://github.com/iamSahdeep/Bop/issues/new?template=feature_request.md)

In order to help the developer understand the feature request;

- Title of the issue should be explicit, giving insight into the content of the issue.
- The area of the project where your feature would be applied or implemented should be properly stated. Add screenshots of mockup if possible.
- It would be great if a detailed use case is included in your request.

***You can as well utilize the already laid down "feature request" template on the post editor.***


When submitting a feature request, please make a single issue for each feature request (i.e. don't submit an issue that contains a list of features). Such issues are hard to keep track of and often get lost.


## Bug & Crash Reports.

Firstly, I apologize for any inconvenience this issue may have caused you. I'm working to ensure that the app is bug-free. You can help speed up that process by a report to me when you encounter an error, or when the app crashes. 

Filing a great bug report helps the developer pinpoint the cause of the bug and effectively work on a fix.

### Steps on how to file a great bug report.

Before filing a bug report;

- Check the issue tracker if the bug hasn't been reported by other users. If it has been reported before it is likely to be in [opened issues](https://github.com/iamSahdeep/Bop/issues). Also, check closed issues too.
- Ensure you're running the latest version of the software
- Confirm if it's actually a bug and not an error caused by a plugin on your system. Test with other systems to verify
- If the same issue persists after testing on other devices then it is indeed a bug. 


## Commit Guidelines

The developer encourages more small commits over one large commit. Small, focused commits make the review process easier and are more likely to be accepted. It is also important to summarise the changes made with brief commit messages. If the commit fixes a specific issue, it is also good to note that in the commit message.

The commit message should start with a single line that briefly describes the changes. That should be followed by a blank line and then a more detailed explanation. As a good practice, use commands when writing the message (instead of "I added ..." or "Adding ...", use "Add ...").

Before committing check for unnecessary whitespace with `git diff --check`.

For further recommendations, see [Pro Git Commit Guidelines](https://git-scm.com/book/en/v2/Distributed-Git-Contributing-to-a-Project#Commit-Guidelines "Pro Git Commit Guidelines").

## Submitting Changes

### Pull Request Guidelines

The following guidelines can increase the likelihood that your pull request will get accepted:

* Work on topic branches.
* Follow the commit guidelines.
* Keep the patches on topic, focused, and atomic.
* Try to avoid unnecessary formatting and clean-up where reasonable.

A pull request should contain the following:

* At least one commit (all of which should follow the Commit Guidelines)
* Title that summarises the issue
* Description that briefly summarises the changes

After submitting a pull request, you should get a response within the next 7 days. If you do not, don't hesitate to ping the thread.

## Creating a pull request

If you don't know how to create a pull request, this section will help you to get started. 

Here's a detailed content on how to [Create a pull request](https://help.github.com/articles/creating-a-pull-request)

Simply put, the way to create a Pull request is first to; 

1. Fork the repository of the project which in this case is [Bop Music Player](https://github.com/iamSahdeep/Bop/)
2. Commit modifications and changes to your fork
3. Send a [pull request](https://help.github.com/articles/creating-a-pull-request) to the original repository you forked your repository from in step 1


## Code Contribtuion.

Do you have ideas of some new cool functionalities, a bug fix or other code you wish to contribute? This is the perfect section to guide you on that path.

#### Test Your Code

There are four possible tests you can run to verify your code.  The first
is unit tests, which check the basic functionality of the application, and
can be run by gradle using:

    # ./gradlew testReleaseUnitTest

The second and third check for common problems using static analysis.
These are the Android lint checker, run using:

    # ./gradlew lintRelease

and FindBugs, run using:

    # ./gradlew findbugs

The final check is by testing the application on a live device and verifying
the basic functionality works as expected.

#### Make Sure Your Code is Tested

Bop music player code uses a fair number of unit tests to verify that the basic functionality is working. Submissions which add functionality or significantly change the existing code should include additional tests to verify the proper operation of the proposed changes.

#### Explain Your Work

At the top of every patch, you should include a description of the problem you are trying to solve, how you solved it, and why you chose the solution you implemented. If you are submitting a bug fix, it is also incredibly helpful if you can describe/include a reproducer for the problem in the description as well as instructions on how to test for the bug and verify that it has been
fixed.

## Documentation.

This is the creation of vital documents which are necessary for the software. Documentation also deals with written content creation. 
For better implementation, ensure to create the document with markdown text styling.
Rename the file with respect to content and add ```.md``` at the end so markdown is effective.
Send a pull request after the document is created.

## Contact.

For further inquiries, you can contact the developer via email. Send an email to sahdeepsingh98@gmail.com. The developer can also be contacted by opening an issue on the repository.

You can also check out the developer's profile [here](https://github.com/iamSahdeep).


***Thank you for your interest in contributing to Bop music player. I appreciate all help with finding and fixing bugs, making performance improvements, and other tasks. Every contribution is helpful and I thank you for your effort.***


