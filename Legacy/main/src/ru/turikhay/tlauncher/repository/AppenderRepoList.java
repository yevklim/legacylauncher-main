package ru.turikhay.tlauncher.repository;

public class AppenderRepoList extends RepoList {
    public AppenderRepoList(String name, String[] prefixList) {
        super(name);

        addAll((IRepo[]) AppenderRepo.fromString(prefixList));
    }
}
