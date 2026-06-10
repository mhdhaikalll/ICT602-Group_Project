# Git Guide for ITT588 Group Project

This guide will help team members learn Git and contribute to our project repository.

---

## 📋 Table of Contents

1. [Initial Setup (One-Time Only)](#1-initial-setup-one-time-only)
2. [Clone the Repository](#2-clone-the-repository)
3. [Create Your Own Branch](#3-create-your-own-branch)
4. [Making Changes](#4-making-changes)
5. [Commit Your Changes](#5-commit-your-changes)
6. [Push Your Branch](#6-push-your-branch)
7. [Daily Workflow](#7-daily-workflow)
8. [Common Commands Reference](#8-common-commands-reference)
9. [Troubleshooting](#9-troubleshooting)

---

## 1. Initial Setup (One-Time Only)

### Step 1: Install Git

Download and install Git from: https://git-scm.com/downloads

### Step 2: Configure Your Identity

Open your terminal (Command Prompt, PowerShell, or Git Bash) and run:

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

**Example:**
```bash
git config --global user.name "Ahmad Ali"
git config --global user.email "ahmad@student.uitm.edu.my"
```

---

## 2. Clone the Repository

### Step 1: Get the Repository URL

Repository URL: `https://github.com/mhdhaikalll/ICT602-Group_Project.git`

### Step 2: Clone to Your Computer

1. Open terminal and navigate to where you want the project folder:
   ```bash
   cd Desktop
   ```

2. Clone the repository:
   ```bash
   git clone https://github.com/mhdhaikalll/ICT602-Group_Project.git
   ```

3. Enter the project folder:
   ```bash
   cd ITT588-Group-Project
   ```

---

## 3. Create Your Own Branch

⚠️ **IMPORTANT:** Never work directly on the `main` branch!

### Step 1: Make Sure You're on Main Branch

```bash
git checkout main
```

### Step 2: Get Latest Updates

```bash
git pull origin main
```

### Step 3: Create Your Branch

Create a new branch with your name or feature:

```bash
git checkout -b your-branch-name
```

**Naming Convention Examples:**
- `feature/ahmad-login-page`
- `feature/siti-about-page`
- `fix/ali-navbar-bug`
- `update/maria-homepage`

**Example:**
```bash
git checkout -b feature/ahmad-login-page
```

### Step 4: Verify Your Branch

Check which branch you're on:
```bash
git branch
```

The branch with `*` is your current branch.

---

## 4. Making Changes

Now you can edit files in the project! Use VS Code or any editor you prefer.

### Check Your Changes

After making changes, see what files you modified:

```bash
git status
```

This will show:
- 🔴 **Red files** = Modified but not staged
- 🟢 **Green files** = Staged and ready to commit

---

## 5. Commit Your Changes

### Step 1: Stage Your Changes

**Add specific file:**
```bash
git add filename.html
```

**Add all changed files:**
```bash
git add .
```

### Step 2: Commit with a Message

```bash
git commit -m "Your descriptive message here"
```

**Good Commit Message Examples:**
- `"Add login form to login page"`
- `"Fix navigation bar responsive issue"`
- `"Update homepage hero section"`
- `"Add contact page styling"`

**Bad Examples (avoid these):**
- `"update"`
- `"fix bug"`
- `"changes"`

---

## 6. Push Your Branch

### First Time Pushing Your Branch

```bash
git push -u origin your-branch-name
```

**Example:**
```bash
git push -u origin feature/ahmad-login-page
```

### Subsequent Pushes

After the first push, you can simply use:
```bash
git push
```

---

## 7. Daily Workflow

Every time you start working on the project:

### Step 1: Open Terminal in Project Folder

### Step 2: Switch to Your Branch
```bash
git checkout your-branch-name
```

### Step 3: Get Latest Updates from Main
```bash
git pull origin main
```

### Step 4: Make Your Changes

Edit your files...

### Step 5: Save Your Work
```bash
git add .
git commit -m "Describe what you did"
git push
```

---

## 8. Common Commands Reference

| Command | Description |
|---------|-------------|
| `git status` | Check current status and changed files |
| `git branch` | List all local branches |
| `git branch -a` | List all branches (local + remote) |
| `git checkout branch-name` | Switch to a branch |
| `git checkout -b new-branch` | Create and switch to new branch |
| `git add .` | Stage all changes |
| `git add filename` | Stage specific file |
| `git commit -m "message"` | Commit staged changes |
| `git push` | Push commits to remote |
| `git pull origin main` | Get latest from main branch |
| `git log --oneline` | View commit history |
| `git diff` | See unstaged changes |

---

## 9. Troubleshooting

### "I made changes on the wrong branch!"

If you haven't committed yet:
```bash
git stash
git checkout correct-branch-name
git stash pop
```

### "I need to undo my last commit"

```bash
git reset --soft HEAD~1
```

### "I have merge conflicts"

1. Open the conflicting files
2. Look for conflict markers:
   ```
   <<<<<<< HEAD
   Your changes
   =======
   Other changes
   >>>>>>> branch-name
   ```
3. Manually edit to keep the correct code
4. Remove the conflict markers
5. Save, add, and commit:
   ```bash
   git add .
   git commit -m "Resolve merge conflicts"
   ```

### "My push was rejected"

Get the latest changes first:
```bash
git pull origin your-branch-name
```
Then push again:
```bash
git push
```

---

## 📝 Quick Start Summary

```bash
# 1. Clone (first time only)
git clone https://github.com/mhdhaikalll/ITT588-Group-Project.git
cd ITT588-Group-Project

# 2. Create your branch
git checkout main
git pull origin main
git checkout -b feature/your-name-feature

# 3. Work and commit
# ... make your changes ...
git add .
git commit -m "Describe your changes"

# 4. Push your work
git push -u origin feature/your-name-feature
```

---

## 🔔 Important Notes

1. **Always work on your own branch** - Never commit directly to `main`
2. **Pull from main regularly** - Keep your branch updated
3. **Write clear commit messages** - Help everyone understand changes
4. **Push your work daily** - Don't lose your progress
5. **Ask for help** - If stuck, ask your team leader!

---

## 👤 After You Push

Once you push your branch, notify the team leader (repository owner) to review and merge your changes through a Pull Request (PR).

**Your responsibilities:**
- Create your branch ✅
- Make changes ✅
- Commit with good messages ✅
- Push to your branch ✅

**Team leader responsibilities:**
- Review your code
- Approve and merge PR
- Handle conflicts if any

---

*Last updated: December 2024* </br>
*Generated by Claude Sonnet 4.5*
