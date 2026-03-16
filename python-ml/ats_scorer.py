# ats_scorer.py
# This script is called by Java (ATSService.java) using ProcessBuilder.
# It takes resume text and required skills as input,
# calculates ATS score and returns result as JSON.

import sys
import json
import re


def clean_text(text):
    """
    Clean and normalize text.
    Convert to lowercase and remove extra spaces.
    """
    text = text.lower()
    text = re.sub(r'\s+', ' ', text)  # remove extra spaces
    text = text.strip()
    return text


def calculate_keyword_score(resume_text, skills_list):
    """
    Calculate score based on keyword matching.
    Check how many required skills are present in resume.
    Returns score as percentage.
    """
    if not skills_list:
        return 0.0

    resume_clean = clean_text(resume_text)
    matched_count = 0

    for skill in skills_list:
        skill_clean = clean_text(skill)
        if skill_clean in resume_clean:
            matched_count += 1

    score = (matched_count / len(skills_list)) * 100
    return round(score, 2)


def calculate_word_overlap_score(resume_text, required_skills):
    """
    Calculate score based on word overlap between
    resume text and required skills.
    This gives a more accurate similarity score.
    """
    # Get all words from resume
    resume_words = set(re.findall(r'\b\w+\b',
                                   resume_text.lower()))

    # Get all words from required skills
    skill_words = set(re.findall(r'\b\w+\b',
                                  required_skills.lower()))

    if not skill_words:
        return 0.0

    # Find common words
    common_words = resume_words & skill_words

    # Score = common words / total skill words
    score = (len(common_words) / len(skill_words)) * 100
    return round(score, 2)


def get_matched_skills(resume_text, skills_list):
    """
    Return list of skills found in resume.
    """
    resume_clean = clean_text(resume_text)
    matched = []

    for skill in skills_list:
        skill_clean = clean_text(skill)
        if skill_clean in resume_clean:
            matched.append(skill.strip())

    return matched


def get_missing_skills(resume_text, skills_list):
    """
    Return list of skills not found in resume.
    """
    resume_clean = clean_text(resume_text)
    missing = []

    for skill in skills_list:
        skill_clean = clean_text(skill)
        if skill_clean not in resume_clean:
            missing.append(skill.strip())

    return missing


def get_score_label(score):
    """
    Return label based on score percentage.
    """
    if score >= 75:
        return "Excellent Match"
    elif score >= 50:
        return "Good Match"
    elif score >= 25:
        return "Average Match"
    else:
        return "Low Match"


def calculate_ats_score(resume_text, required_skills):
    """
    Main function to calculate final ATS score.
    Combines keyword score and word overlap score.
    Returns result as dictionary.
    """

    # Split required skills by comma
    skills_list = [s.strip() for s in required_skills.split(',')
                   if s.strip()]

    if not skills_list:
        return {
            "score": 0.0,
            "keyword_score": 0.0,
            "overlap_score": 0.0,
            "matched": [],
            "missing": [],
            "label": "Low Match"
        }

    # Calculate keyword matching score
    keyword_score = calculate_keyword_score(resume_text, skills_list)

    # Calculate word overlap score
    overlap_score = calculate_word_overlap_score(
        resume_text, required_skills)

    # Final score = 70% keyword + 30% overlap
    final_score = (keyword_score * 0.70) + (overlap_score * 0.30)
    final_score = round(final_score, 2)

    # Get matched and missing skills
    matched = get_matched_skills(resume_text, skills_list)
    missing = get_missing_skills(resume_text, skills_list)

    # Get score label
    label = get_score_label(final_score)

    return {
        "score":         final_score,
        "keyword_score": keyword_score,
        "overlap_score": overlap_score,
        "matched":       matched,
        "missing":       missing,
        "label":         label
    }


# ========================
# Main - called by Java
# ========================
if __name__ == "__main__":

    # Check arguments
    if len(sys.argv) < 3:
        print(json.dumps({
            "score": 0.0,
            "keyword_score": 0.0,
            "overlap_score": 0.0,
            "matched": [],
            "missing": [],
            "label": "Low Match",
            "error": "Missing arguments. Usage: python ats_scorer.py <resume_text> <skills>"
        }))
        sys.exit(1)

    # Get resume text and skills from arguments
    resume_text    = sys.argv[1]
    required_skills = sys.argv[2]

    # Calculate score
    result = calculate_ats_score(resume_text, required_skills)

    # Print result as JSON - Java will read this output
    print(json.dumps(result))