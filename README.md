# Version Cleaner

[![Unit tests](https://github.com/credible-team/action-version-cleaner/actions/workflows/unit-test.yml/badge.svg)](https://github.com/credible-team/action-version-cleaner/actions/workflows/publish-snapshot.yml)

Github Action to clean version tags from a repository. It works both with USER and ORGANIZATION repositories.

## Usage

Include the following step in your workflow to delete all SNAPSHOT packages from your repository.

```yaml
- name: Delete SNAPSHOT packages
  uses: credible-team/action-version-cleaner@main
  with:
    github-repository: ${{ github.repository }}
    package-type: maven
    version-tag: SNAPSHOT
    is-version-tag-strict: true
    github-token: ${{ secrets.DELETE_PACKAGES_TOKEN }}
```

## Inputs

- github-repository: The repository to clean. Not Required. Default: ```${{ github.repository }}```
- package-type: The type of package to clean. Required.
- version-tag: The version tag to clean. Not Required. Default: SNAPSHOT
- is-version-tag-strict: Whether to use strict matching for version tag. Not Required. Default: true
- github-token: The token to use to authenticate with Github API. Required. See [Token generation](#token-generation)
  for more information.

## Token generation

Since this action deletes packages, it requires a token with write and delete permissions. Follow the steps in official
documentation
to [create a personal access token](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token)
with the following permissions:

- read:packages
- delete:packages
- read:project
- repo (if package-type is container)

After creating a new token, do not forget to add it to
your [repository's secrets](https://docs.github.com/en/actions/security-guides/using-secrets-in-github-actions). Then,
use it in your workflow as shown in the [Usage](#usage) section.

P.S. Default ```${{ secrets.GITHUB_TOKEN }}``` is not suitable for this action, because it has not enough permissions to
read a package's repository.

