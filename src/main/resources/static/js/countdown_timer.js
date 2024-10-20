document.addEventListener("DOMContentLoaded", function() {
    const timeRemainingElement = document.getElementById('time-remaining');
    const duration1 = parseInt(document.body.getAttribute('data-duration'), 10); // Extract duration from body attribute
    const duration = parseInt(document.getElementById("test-duration").value, 10);
    console.log("Duration is:", duration);

    // Ensure duration is a valid number
    if (isNaN(duration) || duration <= 0) {
        timeRemainingElement.textContent = "Invalid test duration.";
        return;
    }

    let timeLeft = duration * 60;// Initial time remaining in seconds

    // Function to format time into HH:MM:SS
    function formatTime(seconds) {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;

        // Ensure two-digit format for minutes and seconds
        return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }

    // Update the time remaining every second
    const timerInterval = setInterval(() => {
        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            alert("Time is up! The test will be submitted.");
            // Auto-submit form or take necessary action here
            document.querySelector('form').submit();
        } else {
            timeRemainingElement.textContent = formatTime(timeLeft);
            timeLeft--; // Reduce the time left by one second
        }
    }, 1000);

    // Initial display of the remaining time
    timeRemainingElement.textContent = formatTime(timeLeft);
});
