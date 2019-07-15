# Contributing

Thank you for choosing to contribute to AEM.Design project, we really appreciate your time and effort! 😃🎊

The following are a set of guidelines for contributing to the project.

#### Contents

* [Code of Conduct](#code-of-conduct)
* [Ways of Contributing](#ways-of-contributing)
  * [Joining Feature Discussions](#joining-feature-discussions-) 💭
  * [Reporting Bugs](#reporting-bugs-) 🐛
  * [Requesting Features](#requesting-features-) 🚀
  * [Contributing Code](#contributing-code-) 👾
  * [Reviewing Code](#reviewing-code-) 👀
  * [Documenting](#documenting-) 📜
* [Issue Report Guidelines](#issue-report-guidelines)

## Code of Conduct

This project adheres to the [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to the team.

## Ways of Contributing

There are many ways of contributing, from testing and reporting an issue to suggesting and coding full components or features. Below is a summary of some of the best ways to get involved. 

### Joining Feature Discussions 💭

You can add your voice to discussions around new and existing component features by commenting on an Enhancements. New components and features that openly invite public comment are marked by an [enhancement](https://github.com/aem-design/aemdesign-aem-core/labels/enhancement) (Enhancement) label. We strongly encourage users of the framework to bring their own project experience to these issues, as there may be alternative use-cases or requirements that haven't yet been considered.


### Reporting Bugs 🐛

##### Before Reporting a Bug 

* Have a quick search through the currently open [bug reports](https://github.com/aem-design/aemdesign-aem-core/labels/bug) to see if the issue has already been reported.
* Ensure that the issue is repeatable on a clean instance and that the actual behavior versus the expected results can be easily described.
* Check that the issue you are experiencing is related to the aem.design framework. It may be that the problem derives from AEM itself, Core Component or ACS Commons. If you're not sure, then feel free to report the issue anyway and the committers will clarify for you.

##### Filing a Bug

1. Visit our [issue tracker on GitHub](https://github.com/aem-design/aemdesign-aem-core/issues).
1. File a `New Issue` as a `Bug Report`.
1. Ensure your issue follows the [issue report guidelines](#issue-report-guidelines).
1. Thanks for the report! The committers will get back to you in a timely manner, typically within one week.

### Requesting Features 🚀

##### Before Requesting a Feature

* Have a quick search through the currently open [enhancement](https://github.com/aem-design/aemdesign-aem-core/labels/enhancement) issues to see if the idea has already been suggested. If it has, you may still have a slightly different requirement that isn't covered, in which case, feel free to comment on the open issue. 
* Take a look at the [Roadmap](https://github.com/aem-design/aemdesign-aem-core/wiki/Road-Map) to see if your feature is already on the radar. If it is and doesn't have a public issue yet, feel free to create one, listing your own requirements.
* Consider whether your requirement is generically useful rather than project-specific and would therefore benefit all users of the Core Components.

##### Requesting a Feature

1. Visit our [issue tracker on GitHub](https://github.com/aem-design/aemdesign-aem-core/issues).
1. File a `New Issue` as a `Enhancement`.
1. Ensure your issue follows the [issue report guidelines](#issue-report-guidelines).
1. Thanks for the feature request! The committers will get back to you in a timely manner, typically within one week.

### Contributing Code 👾 

High quality code is important to the project, and to keep it that way, all code submissions are reviewed by committers before being accepted. A close adherence to the guidelines below can help speed up the review process and increase the likelihood of the submission being accepted.

##### Before Contributing

* Consider [joining developer discussions](#joining-developer-discussions-) to get feedback on what you are thinking of contributing. It's better to get this early feedback before going ahead and potentially having to rewrite everything later.
* Create a [bug report](#reporting-bugs-) or [feature request](#requesting-features-) issue summarizing the problem that you will be solving. This will again help with early feedback and tracking.
* Have a look at our [component checklist](GUIDELINES.md), for an idea of what certifies a production ready component.

##### Contributing

The project accepts contributions primarily using GitHub pull requests. This process:
* Helps to maintain project quality
* Engages the community in working towards commonly accepted solutions with peer review
* Leads to a more meaningful and cleaner git history
* Ensures sustainable code management 

Creating a pull request involves creating a fork of the project in your personal space, adding your new code in a branch and triggering a pull request. Check the GitHub [Using Pull Requests](https://help.github.com/articles/using-pull-requests) article on how to perform pull requests.

The title of the pull request typically matches that of the issue it fixes, see the [issue report guidelines](#issue-report-guidelines).
Have a look at our [pull request template](.github/pull_request_template.md) to see what is expected to be included in the pull request description. The same template is available when the pull request is triggered. 

##### Your first contribution
Would you would like to contribute to the project but don't have an issue in mind? Or are still fairly unfamiliar with the code? Then have a look at our [good first issues](https://github.com/aem-design/aemdesign-aem-core/labels/good%20first%20issue), they are fairly simple starter issues that should only require a small amount of code and simple testing.

### Reviewing Code 👀

Reviewing others' code contributions is another great way to contribute - more eyes on the code help to improve its overall quality. To review a pull request, check the [open pull requests](https://github.com/aem-design/aemdesign-aem-core/labels/pulls) for anything you can comment on. 

### Documenting 📜

We very much welcome issue reports or pull requests that improve our documentation pages. While the best effort is made to keep them error free, useful and up-to-date there are always things that could be improved. The component documentation pages (for example the [Text Component Documentation](https://github.com/aem-design/aemdesign-aem-core/blob/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/text/v2/text/README.md)), this contributing guide or our [GitHub Wiki](https://github.com/aem-design/aemdesign-aem-core/wiki) pages are good places to start.

## Issue Report Guidelines

A well defined issue report will help in quickly understanding and replicating the problem faced, or the feature requested. Below are some guidelines on what to include when reporting an issue. You can also see [this community reported issue](https://github.com/adobe/aem-core-wcm-components/issues/247) for an example of a well written issue report.

##### Title
* **Descriptive** - Should be specific, well described and readable at a glance.
* **Concise** - If the issue can't be easily described in a short title, then it is likely unfocused.
* **Keyword-rich** - Including keywords can help with quickly finding the issue in the backlog. Component related issues can be prefixed with a bracketed label with the component name, for example `[Image]` for the image component.

Bad title: `Search component has security problems`  
Good title: `[Search] Fulltext search of pages might lead to DDOS`

##### Description

See our [bug report template](.github/ISSUE_TEMPLATE/bug_report.md) or [feature request template](.github/ISSUE_TEMPLATE/feature_request.md) for details on what is expected to be described. The same information is available when creating a new issue on GitHub.

##### Labels

Once an issue is reported, the project committers will assign it a relevant label. You can see our [label list on GitHub](https://github.com/aem-design/aemdesign-aem-core/labels) to better understand what each label means.
