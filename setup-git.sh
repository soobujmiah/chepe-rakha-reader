#!/bin/bash
###############################################################################
# Chepe Rakha Reader — Git Setup Script
# Run this after cloning the repository
###############################################################################

set -e

echo "=== Chepe Rakha Reader — Initial Setup ==="

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "Initializing git repository..."
    git init
fi

# Add remote if not exists
REMOTE=$(git remote get-url origin 2>/dev/null || echo "")
if [ -z "$REMOTE" ]; then
    echo ""
    read -p "Enter GitHub repository URL (e.g., https://github.com/user/repo.git): " REMOTE
    git remote add origin "$REMOTE"
fi

# Check if EPUB exists
if [ ! -f "app/src/main/assets/book/book.epub" ]; then
    echo ""
    echo "WARNING: EPUB file not found at app/src/main/assets/book/book.epub"
    echo "Please add your EPUB file there."
    read -p "Press Enter to continue anyway..."
fi

# Stage all files
git add .

# Check status
echo ""
echo "Files to commit:"
git status --short

echo ""
echo "To complete the setup, run:"
echo "  git commit -m 'Initial commit: Chepe Rakha Reader'"
echo "  git push -u origin main"
