import os
import json
import requests
import subprocess
from pathlib import Path
import sys

def detect_platform():
    """Detect if running in GitHub Actions or Azure DevOps"""
    if os.getenv('GITHUB_ACTIONS'):
        return 'github'
    elif os.getenv('SYSTEM_TEAMFOUNDATIONCOLLECTIONURI'):
        return 'azure'
    else:
        return 'local'

def run_java_analysis():
    """Run Java-specific static analysis"""
    print("🔍 Running Java code analysis...")

    results = {
        'complexity': {},
        'security': {},
        'quality': {},
        'metrics': {}
    }

    # Find all Java files
    java_files = list(Path('.').rglob('*.java'))

    for java_file in java_files:
        print(f"Analyzing: {java_file}")

        try:
            with open(java_file, 'r', encoding='utf-8') as f:
                code = f.read()
                lines = code.split('\n')
                results['metrics'][str(java_file)] = {
                    'lines_of_code': len(lines),
                    'file_size_kb': len(code) / 1024,
                    'has_spring_annotations': any(annotation in code for annotation in
                        ['@RestController', '@Controller', '@Service', '@Repository'])
                }
        except Exception as e:
            print(f"⚠️ Could not analyze {java_file}: {e}")

    return results

def check_spring_best_practices():
    """Check Spring-specific best practices"""
    practices = {
        'controller_annotations': False,
        'service_annotations': False,
        'repository_annotations': False,
        'dependency_injection': False,
        'configuration_annotations': False
    }

    spring_patterns = {
        'controller_annotations': ['@RestController', '@Controller'],
        'service_annotations': ['@Service'],
        'repository_annotations': ['@Repository'],
        'dependency_injection': ['@Autowired', '@Inject', '@Resource'],
        'configuration_annotations': ['@Configuration', '@SpringBootApplication']
    }

    for file_path in Path('.').rglob('*.java'):
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                for practice, patterns in spring_patterns.items():
                    if any(pattern in content for pattern in patterns):
                        practices[practice] = True
        except Exception as e:
            print(f"⚠️ Could not check Spring practices in {file_path}: {e}")
            continue

    return practices

def run_security_checks():
    """Run basic security checks"""
    security_issues = []

    # Check for common security issues in Java files
    for file_path in Path('.').rglob('*.java'):
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

                # More specific security checks
                if 'System.out.println' in content and any(tag in content for tag in ['TODO', 'FIXME', 'DEBUG']):
                    security_issues.append(f"Potential debug code left in production: {file_path}")

                # Check for hardcoded credentials patterns
                if any(credential_keyword in content.lower() for credential_keyword in
                       ['password=', 'passwd=', 'pwd=', 'key=']) and not any(safe_indicator in content for safe_indicator in
                       ['// TODO:', 'FIXME', 'EXAMPLE']):
                    security_issues.append(f"Potential hardcoded credentials in {file_path}")

        except Exception as e:
            print(f"⚠️ Could not run security check on {file_path}: {e}")
            continue

    return security_issues

def generate_report(analysis_results, spring_practices, security_issues):
    """Generate PR review report"""
    total_files = len(analysis_results['metrics'])
    total_lines = sum(m['lines_of_code'] for m in analysis_results['metrics'].values())

    report = {
        'summary': 'Java Spring Code Analysis Report',
        'files_analyzed': total_files,
        'total_lines_of_code': total_lines,
        'spring_best_practices': spring_practices,
        'security_issues': security_issues,
        'recommendations': [],
        'platform': detect_platform()
    }

    # Add recommendations
    if not spring_practices['controller_annotations'] and total_files > 0:
        report['recommendations'].append(
            "Consider using @RestController or @Controller annotations for web endpoints"
        )

    if not spring_practices['dependency_injection'] and total_files > 0:
        report['recommendations'].append(
            "Use dependency injection (@Autowired) instead of manual object creation"
        )

    if total_lines > 1000:
        report['recommendations'].append(
            "Consider breaking down large codebase into smaller microservices"
        )

    if security_issues:
        report['recommendations'].extend(security_issues)

    if not report['recommendations']:
        report['recommendations'].append("✅ Code looks good! No major issues found.")

    return report

def post_github_comment(report):
    """Post comment to GitHub PR"""
    repo = os.getenv('GITHUB_REPOSITORY')
    github_ref = os.getenv('GITHUB_REF', '')
    github_token = os.getenv('GITHUB_TOKEN')

    if '/pull/' in github_ref:
        pr_number = github_ref.split('/')[2]
    else:
        print("Not in PR context, skipping GitHub comment")
        return

    comment = f"""
## 🤖 Automated Code Analysis Report ({report['platform'].upper()})

### 📊 Summary
- **Files Analyzed**: {report['files_analyzed']}
- **Total Lines**: {report['total_lines_of_code']}

### ✅ Spring Best Practices
{chr(10).join(f"- **{k.replace('_', ' ').title()}**: {'✅' if v else '❌'}" for k, v in report['spring_best_practices'].items())}

### 🔒 Security Check
{chr(10).join(f"- ⚠️ {issue}" for issue in report['security_issues']) if report['security_issues'] else '- ✅ No security issues found'}

### 💡 Recommendations
{chr(10).join(f'- {rec}' for rec in report['recommendations'])}

---
*Generated by PR Analyzer running on {report['platform'].title()}*
"""

    url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"
    headers = {
        'Authorization': f'token {github_token}',
        'Accept': 'application/vnd.github.v3+json'
    }

    try:
        response = requests.post(url, headers=headers, json={'body': comment})
        if response.status_code == 201:
            print("✅ Comment posted to GitHub PR")
        else:
            print(f"❌ Failed to post GitHub comment: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"❌ Error posting to GitHub: {e}")

def post_azure_comment(report):
    """Post comment to Azure DevOps PR"""
    pr_id = os.getenv('SYSTEM_PULLREQUEST_PULLREQUESTID')
    token = os.getenv('SYSTEM_ACCESSTOKEN')
    org_url = os.getenv('SYSTEM_TEAMFOUNDATIONCOLLECTIONURI')
    project = os.getenv('SYSTEM_TEAMPROJECT')

    if not all([pr_id, token]):
        print("Not in Azure PR context, skipping Azure comment")
        return

    comment = f"""
## 🤖 Automated Code Analysis Report ({report['platform'].upper()})

### 📊 Summary
- **Files Analyzed**: {report['files_analyzed']}
- **Total Lines**: {report['total_lines_of_code']}

### ✅ Spring Best Practices
{chr(10).join(f"- **{k.replace('_', ' ').title()}**: {'✅' if v else '❌'}" for k, v in report['spring_best_practices'].items())}

### 🔒 Security Check
{chr(10).join(f"- ⚠️ {issue}" for issue in report['security_issues']) if report['security_issues'] else '- ✅ No security issues found'}

### 💡 Recommendations
{chr(10).join(f'- {rec}' for rec in report['recommendations'])}

---
*Generated by PR Analyzer running on {report['platform'].title()}*
"""

    print("Azure DevOps Comment Preview:")
    print(comment)
    print("✅ Analysis complete for Azure DevOps!")

def main():
    print(f"🚀 Starting PR Code Analysis on {detect_platform().upper()}...")

    # Run analysis
    analysis_results = run_java_analysis()
    spring_practices = check_spring_best_practices()
    security_issues = run_security_checks()

    # Generate report
    report = generate_report(analysis_results, spring_practices, security_issues)

    # Output results
    print(json.dumps(report, indent=2))

    # Post to appropriate platform
    platform = detect_platform()
    if platform == 'github':
        post_github_comment(report)
    elif platform == 'azure':
        post_azure_comment(report)
    else:
        print("Local execution - no PR comments posted")

    print(f"✅ Analysis complete on {platform.upper()}!")

if __name__ == "__main__":
    main()