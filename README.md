## Reinvent 2018 Social News App

Build a social news app.

## Install AWS Amplify CLI

You will need th cli to provision your backend resources

```
npm install -g @aws-amplify/cli
```

After it has been installed configure it with your preferences

```
amplify configure
```

## Clone the repo

Find a workspace to clone the repository.

```
git clone git@github.com:aws-samples/reinvent-2018-social-news-app.git
```

Change your directory into the project

```
cd reinvent-2018-social-news-app/SocialNews
```

## Provision your backend

### Initialize your project

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

### Add auth to your project

This will add Amazon Cognito Userpools and Amazon Cognito Identity pools to your project.

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

### Add API (data) to your project

This will add AWS AppSync and provision a data source to store your news articles and comments in Amazon DynamoDB.

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

## Adding sign-in

Inside the MainActivity.java replace

```
 // TODO Add sign-in sign-out code
```

with

```
                if (AWSMobileClient.getInstance().isSignedIn()) {
                    AWSMobileClient.getInstance().signOut();
                    item.setTitle("Sign-in");
                    ClientFactory.getAnalyticsClient().recordEvent(
                            ClientFactory.getAnalyticsClient()
                                    .createEvent("ui")
                                    .withAttribute("clicked", "sign-out"));
                    break;
                }

                ClientFactory.getAnalyticsClient().recordEvent(
                        ClientFactory.getAnalyticsClient()
                                .createEvent("ui")
                                .withAttribute("clicked", "sign-in"));
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
## Adding API (data)

Inside NewsRepository.java replace

```
// TODO Add api (data) to list the news articles
```

with

```
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

## Adding analytics events

Inside the DetailedActivity.java replace

```
// TODO Add analytics to your app
```

with

```
        ClientFactory.getAnalyticsClient().recordEvent(
                ClientFactory.getAnalyticsClient().createEvent("ui").withAttribute("clicked", "newsId-" + newsId)
        );
```

## License Summary

This sample code is made available under a modified MIT license. See the LICENSE file.
