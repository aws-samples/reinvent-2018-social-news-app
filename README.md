## Reinvent 2018 Social News App

Build a social news app.

Tools you will need:
- [Android Studio 3.2](https://developer.android.com/studio/)
  - Install an emulator for API level 28 *or* you may use a real device with Android Marshmallow and above
- [Node.js/npm v8+](https://nodejs.org)
- An AWS account

## Install AWS Amplify CLI

You will need the CLI to provision your backend resources.

```
npm install -g @aws-amplify/cli
```

After it has been installed configure it with your preferences.

```
amplify configure
```

Make sure that you save the access and secret key from the console and then input the access and secret keys when prompted by the sentence `Enter the access key of the newly created user:` on the terminal.

Sample input and output:

```
$ amplify configure
Follow these steps to set up access to your AWS account:

Sign in to your AWS administrator account:
https://console.aws.amazon.com/
Press Enter to continue

Specify the AWS Region
? region:  us-west-2
Specify the username of the new IAM user:
? user name:  amplify-Q1HYV
Complete the user creation using the AWS console
https://console.aws.amazon.com/iam/home?region=undefined#/users$new?step=final&accessKey&userNames=amplify-Q1HYV&permissionType=policies&policies=arn:aws:iam::aws:policy%2FAdministratorAccess
Press Enter to continue

Enter the access key of the newly created user:
? accessKeyId:   ABC*****************
? secretAccessKey:  asdf*******************************
This would update/create the AWS Profile in your local machine
? Profile Name:  cli2

Successfully set up the new user.
```

## Clone the repository

Find a workspace to clone the repository.

```
git clone https://github.com/aws-samples/reinvent-2018-social-news-app.git
```

Change your directory into the project.

```
cd reinvent-2018-social-news-app/SocialNews
```

## Initialize your project

This will create an `amplify` folder within your project to keep track of the state of your backend as you add authentication, api, and analytics.

```
amplify init
```

Sample input and output:

```
$ amplify init
Note: It is recommended to run this command from the root of your app directory
? Choose your default editor: Sublime Text
? Choose the type of app that you're building android
Describe your project:
? Where is your Res directory:  app/src/main/res
Using default provider awscloudformation

For more information on AWS Profiles, see:
https://docs.aws.amazon.com/cli/latest/userguide/cli-multiple-profiles.html

? Do you want to use an AWS profile? Yes
? Please choose the profile you want to use profile_cli
⠸ Initializing project in the cloud...

CREATE_IN_PROGRESS ocialews-20181126121957 AWS::CloudFormation::Stack Mon Nov 26 2018 12:19:57 GMT-0800 (PST) User Initiated             
CREATE_IN_PROGRESS AuthRole                AWS::IAM::Role             Mon Nov 26 2018 12:20:01 GMT-0800 (PST)                            
CREATE_IN_PROGRESS UnauthRole              AWS::IAM::Role             Mon Nov 26 2018 12:20:01 GMT-0800 (PST)                            
CREATE_IN_PROGRESS DeploymentBucket        AWS::S3::Bucket            Mon Nov 26 2018 12:20:02 GMT-0800 (PST)                            
CREATE_IN_PROGRESS AuthRole                AWS::IAM::Role             Mon Nov 26 2018 12:20:02 GMT-0800 (PST) Resource creation Initiated
CREATE_IN_PROGRESS DeploymentBucket        AWS::S3::Bucket            Mon Nov 26 2018 12:20:03 GMT-0800 (PST) Resource creation Initiated
CREATE_IN_PROGRESS UnauthRole              AWS::IAM::Role             Mon Nov 26 2018 12:20:03 GMT-0800 (PST) Resource creation Initiated
⠇ Initializing project in the cloud...

CREATE_COMPLETE AuthRole   AWS::IAM::Role Mon Nov 26 2018 12:20:14 GMT-0800 (PST) 
CREATE_COMPLETE UnauthRole AWS::IAM::Role Mon Nov 26 2018 12:20:15 GMT-0800 (PST) 
⠦ Initializing project in the cloud...

CREATE_COMPLETE DeploymentBucket        AWS::S3::Bucket            Mon Nov 26 2018 12:20:23 GMT-0800 (PST) 
CREATE_COMPLETE ocialews-20181126121957 AWS::CloudFormation::Stack Mon Nov 26 2018 12:20:26 GMT-0800 (PST) 
✔ Successfully created initial AWS cloud resources for deployments.

Your project has been successfully initialized and connected to the cloud!
```

## Add auth to your project

This will add Amazon Cognito Userpools and Amazon Cognito Identity pools to your project. Amazon Cognito Userpools will be used to keep track of your users and give them accounts with username and password. Amazon Cognito Identity pools will then give those accounts permissions to access AWS resources like the news articles.

```
amplify add auth
```

Sample input and output:

```
$ amplify add auth
Using service: Cognito, provided by: awscloudformation
 The current configured provider is Amazon Cognito. 
 Do you want to use the default authentication and security configuration? Yes, use the default configuration.
Successfully added resource cognito3da6ae94 locally
```

### Push the configuration to the cloud

```
amplify push
```

## Adding sign-in and sign-out code

Inside the MainActivity.java replace

```java
// TODO Add sign-in sign-out code
```

with

```java
if (AWSMobileClient.getInstance().isSignedIn()) {
    AWSMobileClient.getInstance().signOut();
    item.setTitle("Sign-in");
    // TODO Add analytics event for sign-out, this will be added at a later step
    break;
}

// TODO Add analytics event for sign-in, this will be added at a later step
AWSMobileClient.getInstance().showSignIn(this, new Callback<UserStateDetails>() {
    @Override
    public void onResult(UserStateDetails result) {
        item.setTitle("Sign-out");
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "onError: ", e);
    }
});
```

## Add API (data) to your project

This will add AWS AppSync to front your data and Amazon DynamoDB as a data source to store your news articles and comments. The `model.graphql` file is provided in your project when prompted by `? Do you have an annotated GraphQL schema? Yes` and `? Provide your schema file path: ./model.graphql`.

```
amplify add api
```

Sample input and output:

```
$ amplify add api
? Please select from one of the below mentioned services GraphQL
? Provide API name: test123
? Choose an authorization type for the API API key
? Do you have an annotated GraphQL schema? Yes
? Provide your schema file path: ./model.graphql

GraphQL schema compiled successfully. Edit your schema at /Users/bimin/github/reinvent-2018-social-news-app/SocialNews/amplify/backend/api/test123/schema.graphql
Successfully added resource test123 locally
```

### Push the configuration to the cloud

```
amplify push
```

## Adding API (data) code

Inside NewsRepository.java add code to retrieve the list of news articles by replacing

```java
// TODO Add api (data) to list the news articles
```

with

```java
ListNewssQuery listNewssQuery = ListNewssQuery.builder().build();
client.query(listNewssQuery)
        .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
        .enqueue(new GraphQLCall.Callback<ListNewssQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<ListNewssQuery.Data> response) {
                if (response.hasErrors()) {
                    Log.e(TAG, "onResponse: errors:" + response.errors());
                    return;
                }
                List<News> newsList = marshallListNews(response);
                newsDao.save(newsList);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "Failed to refresh news item", e);
            }
        });
```

## Add analytics to your project

```
amplify add analytics
```

Sample input and output

```
$ amplify add analytics
Using service: Pinpoint, provided by: awscloudformation
? Provide your pinpoint resource name: 307r1
Adding analytics would add the Auth category to the project if not already added.
? Apps need authorization to send analytics events. Do you want to allow guests and unauthentic
ated users to send analytics events? (we recommend you allow this when getting started) true
Successfully added auth resource locally.
Successfully added resource 307r1 locally
```

### Push the configuration to the cloud

```
amplify push
```

### Adding analytics events

#### Sign-in event

Inside the MainActivity.java you can track sign-in events by replacing

```java
// TODO Add analytics event for sign-in
```

with

```java
ClientFactory.getAnalyticsClient().recordEvent(
        ClientFactory.getAnalyticsClient()
                .createEvent("ui")
                .withAttribute("clicked", "sign-in"));
```

#### Sign-out event

Inside the MainActivity.java you can track sign-out events by replacing

```java
// TODO Add analytics event for sign-out
```

with

```java
ClientFactory.getAnalyticsClient().recordEvent(
            ClientFactory.getAnalyticsClient()
                    .createEvent("ui")
                    .withAttribute("clicked", "sign-out"));
```

#### Viewed news event

Inside the DetailedActivity.java you can track which news articles are being looked at by replacing

```java
// TODO Add analytics to your app
```

with

```java
ClientFactory.getAnalyticsClient().recordEvent(
        ClientFactory.getAnalyticsClient().createEvent("ui").withAttribute("clicked", "newsId-" + newsId)
);
```

## Validating your configuration

When you open the app for the first time, you will see a blank screen. This is because there is no data in your Amazon DynamoDB table.

In the upper-right hand corner there is a menu to upload sample data. After the upload, you should refresh the data, again in the upper-right hand corner and then the articles should show up.

## License Summary

This sample code is made available under a modified MIT license. See the LICENSE file.
