let questionIndex = 0;
const optionCounts = {};

const questionTypes = [
    { value: "MULTIPLE_CHOICE", label: "Multiple Choice" },
    { value: "FILL_IN_BLANK", label: "Fill in the Blank" },
    { value: "PICTURE_RESPONSE", label: "Picture response" },
    { value: "MULTI_ANSWER", label: "Multiple Answer"}
];

function typeChange(elem, index) {
    const value = elem.value;
    const target = document.getElementById(`extraFields_${index}`);

    if (value === "MULTIPLE_CHOICE") {
        optionCounts[index] = 2;
        target.innerHTML = `
            <div id="options-${index}">
                <button type="button" onclick="addOption('MULTIPLE_CHOICE', ${index})">Add Option</button>
                <div>
                    <input name="optionText_${index}_0" placeholder="Option 1" />
                    <input type="checkbox" name="isCorrect_${index}_0" /> Correct
                </div>
                <div>
                    <input name="optionText_${index}_1" placeholder="Option 2" />
                    <input type="checkbox" name="isCorrect_${index}_1" /> Correct
                </div>
            </div>
        `;
    } else if (value === "FILL_IN_BLANK") {
        optionCounts[index] = 1;
        target.innerHTML =`
            <div id="options-${index}">
                <button type="button" onclick="addOption('FILL_IN_BLANK', ${index})">Add Option</button>
                <div> 
                    <input name="answer_${index}_0" placeholder="Correct Answer 1" />
                </div> 
            </div>
        `;
    } else if (value === "PICTURE_RESPONSE") {
        optionCounts[index] = 1;
        target.innerHTML =`
            <div id="options-${index}">
                <button type="button" onclick="addOption('PICTURE_RESPONSE', ${index})">Add Option</button>
                <div>
                    <input name="answer_${index}_0" placeholder="Correct Answer_1" />
                    <label for="image_${index}">Upload Image:</label>
                    <input type="file" name="image_${index}" accept="image/*" />
                </div> 
            </div>
        `;

    }else if (value === "MULTI_ANSWER") {
        optionCounts[index] = 1;
        target.innerHTML =`
            <div id="options-${index}">
                <button type="button" onclick="addOption('MULTI_ANSWER', ${index})">Add Option</button>
                <div>                    
                    <input name="answer_${index}_0" placeholder="Correct Answer_1" />
                    <br/>
                </div> 
            </div>
        `;

    }else {
        target.innerHTML = '';
    }
}

function addOption(value, index) {
    const optionIndex = optionCounts[index]++;
    const container = document.createElement("div");
    container.classList.add("option-block");
    let inner = '';
    if(value === "MULTIPLE_CHOICE"){
        inner = `
        <input name="optionText_${index}_${optionIndex}" placeholder="Option ${optionIndex + 1}" />
        <input type="checkbox" name="isCorrect_${index}_${optionIndex}" /> Correct
    `;
    } else if (value === "FILL_IN_BLANK" || value === "PICTURE_RESPONSE" || value === "MULTI_ANSWER") {
        inner = `
        <input name="answer_${index}_${optionIndex}" placeholder="Correct Answer ${optionIndex + 1}" />
    `;
    }
    inner += ` <button type="button" class="remove-option-btn">Remove</button>`;
    container.innerHTML = inner;

    const optionsContainer = document.getElementById(`options-${index}`);
    optionsContainer.appendChild(container);
}

function addQuestionFields() {
    const index = questionIndex++;
    const container = document.createElement("div");
    container.id = `question_${index}`;
    container.innerHTML = `
        <input name="questionText_${index}" placeholder="Question text" />
        <select name="questionType_${index}">
            ${questionTypes.map(type => `<option value="${type.value}">${type.label}</option>`).join('')}
        </select>
        <div id="extraFields_${index}"></div>
        <button type="button" class="delete-question-btn" onclick="deleteQuestion(${index})">Delete</button>
        <hr/>
    `;

    const questionsContainer = document.getElementById("questions-container");
    questionsContainer.appendChild(container);

    const dropdown = container.querySelector(`select[name="questionType_${index}"]`);
    dropdown.addEventListener("change", (e) => typeChange(e.target, index));

    // Default to MULTIPLE_CHOICE
    dropdown.value = "MULTIPLE_CHOICE";
    typeChange(dropdown, index);
}

function deleteQuestion(index) {
    const questionDiv = document.getElementById(`question_${index}`);
    if (questionDiv) {
        questionDiv.remove();
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("add-question-btn").addEventListener("click", addQuestionFields);

    document.getElementById("questions-container").addEventListener("click", function (e) {
        if (e.target.classList.contains("remove-option-btn")) {
            const optionBlock = e.target.closest(".option-block");
            if (optionBlock) optionBlock.remove();
        }
    });

    document.querySelector("form").addEventListener("submit", function (e) {
        for (let i = 0; i < questionIndex; i++) {

            const questionDiv = document.getElementById(`question_${i}`);
            if (!questionDiv) continue;

            const questionText = document.querySelector(`input[name="questionText_${i}"]`);
            if (!questionText || questionText.value.trim() === "") {
                alert(`Question ${i + 1}: Question text is required.`);
                e.preventDefault();
                return;
            }

            const type = document.querySelector(`select[name="questionType_${i}"]`)?.value;
            if (type === "MULTIPLE_CHOICE") {
                const checkboxes = document.querySelectorAll(`input[name^="isCorrect_${i}_"]`);
                const total = checkboxes.length;

                const optionTexts = document.querySelectorAll(`input[name^="optionText_${i}_"]`);
                const unfilled = Array.from(optionTexts).some(opt => opt.value.trim() === "");
                if (unfilled) {
                    alert(`Question ${i + 1}: All option texts must be filled.`);
                    e.preventDefault();
                    return;
                }

                const checkedCount = Array.from(checkboxes).filter(cb => cb.checked).length;
                const uncheckedCount = total - checkedCount;

                if (checkedCount === 0) {
                    alert(`Question ${i + 1}: You must select at least one correct answer.`);
                    e.preventDefault();
                    return;
                }

                if (uncheckedCount === 0) {
                    alert(`Question ${i + 1}: You must include at least one incorrect answer.`);
                    e.preventDefault();
                    return;
                }if (type === "FILL_IN_BLANK") {
                    const answer = document.querySelector(`input[name="answer_${i}_0"]`);
                    if (!answer || answer.value.trim() === "") {
                        alert(`Question ${i + 1}: Fill-in-the-blank answer is required.`);
                        e.preventDefault();
                        return;
                    }
                }
                if (type === "PICTURE_RESPONSE") {
                    const fileInput = document.querySelector(`input[name="image_${i}"]`);
                    const answer = document.querySelector(`input[name="answer_${i}_0"]`);

                    if (!fileInput || fileInput.files.length === 0) {
                        alert(`Question ${i + 1}: You must select an image file.`);
                        e.preventDefault();
                        return;
                    }

                    if (!answer || answer.value.trim() === "") {
                        alert(`Question ${i + 1}: You must enter an answer.`);
                        e.preventDefault();
                        return;
                    }
                }
            }
        }
    });
});

