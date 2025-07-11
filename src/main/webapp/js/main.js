/*
 * Main JavaScript for the Quiz Website
 * Handles interactive elements like the responsive navbar.
 */

document.addEventListener('DOMContentLoaded', () => {
    const menuToggle = document.querySelector('.navbar__menu-toggle');
    const navLinks = document.querySelector('.navbar__links');

    if (menuToggle && navLinks) {
        menuToggle.addEventListener('click', () => {
            menuToggle.classList.toggle('active');
            navLinks.classList.toggle('active');
        });
    }

    // Handle Quiz Progress Bar
    const progressBar = document.querySelector('.progress-bar-inner');
    if (progressBar) {
        const progress = progressBar.getAttribute('data-progress');
        progressBar.style.width = progress + '%';
    }
});