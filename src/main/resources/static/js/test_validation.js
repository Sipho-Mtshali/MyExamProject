document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');  // Get the form element
    const questions = document.querySelectorAll('.question');  // Get all the questions
    const remainingCountElement = document.getElementById('remaining-count');  // Get the element that displays remaining questions

    console.log("Loaded test with", questions.length, "questions.");

    // Initialize the remaining questions count
    let remainingCount = questions.length;
    remainingCountElement.textContent = remainingCount;  // Display the remaining count

    // Function to update the remaining questions count
    function updateRemainingCount() {
        remainingCount = questions.length;

        // Check if each question has at least one checkbox selected
        questions.forEach(function (question) {
            if (question.querySelector('input[type="checkbox"]:checked')) {
                remainingCount--;
            }
        });

        console.log("Remaining unanswered questions:", remainingCount);

        // Update the display with the new count
        remainingCountElement.textContent = remainingCount;
    }

    // Attach event listeners to all checkboxes to detect answer selection
    questions.forEach(function (question) {
        question.querySelectorAll('input[type="checkbox"]').forEach(function (checkbox) {
            checkbox.addEventListener('change', updateRemainingCount);  // Update count when an answer is selected
        });
    });

    // Form submission handling
    form.addEventListener('submit', function (event) {
        if (remainingCount > 0) {
            // Show a warning message if there are unanswered questions
            const confirmSubmission = confirm('You have unanswered questions. Are you sure you want to submit without answering them?');

            // Allow the form to submit regardless of the user's choice
            if (!confirmSubmission) {
                event.preventDefault();  // Only prevent submission if the user clicks "Cancel"
            }
        }
    });
});
