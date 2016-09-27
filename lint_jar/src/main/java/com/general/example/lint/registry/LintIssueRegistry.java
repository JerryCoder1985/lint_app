package com.general.example.lint.registry;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;
import com.general.example.lint.detector.LogDetector;
import com.general.example.lint.detector.ProguardDetector;

import java.util.Arrays;
import java.util.List;

public class LintIssueRegistry extends IssueRegistry {

    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(LogDetector.ISSUE, ProguardDetector.ISSUE);
    }
}
