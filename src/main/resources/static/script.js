async function checkInput() {
    const input = document.getElementById('userInput').value
    const response = await axios.get(
        `/spell-check/${input}`
    )
    console.log(response)
    let correct = response.data["correct"]
    console.log()
    if (correct){
        alert("You entered: " + input + " which is correct phrase");
    }
    else{
        alert("You entered: " + input + " which is incorrect phrase");
    }   
    let container = document.getElementById("history-container")
    container.innerHTML += `
            <div class="history-item">
                <p class="history-item-p">
                    ${input} [${correct}]
                </p>
            </div>
            `
}

async function loadHistory() {
    let container = document.getElementById("history-container")

    let categories = await axios.get("/api/spell-check-categories").data["texts"]
    for (const category of categories.reverse()){
        let checks = await axios.get(`/categories/${category}/spell-checks`).data
        for (const check of checks){
            container.innerHTML += `
            <div class="history-">
                <p class="history-item-p">
                    ${check.name} [${check.status}]
                </p>
            </div>
            `
        }
    }
}