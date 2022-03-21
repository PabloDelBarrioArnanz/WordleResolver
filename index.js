const puppeteer = require('puppeteer')
const fs = require('fs');
const readline = require('readline');

const delay = ms => new Promise(res => setTimeout(res, ms));

const words5 = "./dict/words5.txt"


var matrix
var startsWith = ""
var endsWith = ""
var contains = []
var notContains = ['á', 'é', 'í', 'ó', 'ú']

async function start() {

    const browser = await puppeteer.launch()
    const page = await browser.newPage()
    await page.goto("https://wordle.danielfrg.com/")
    
    //await clickButton(page, "¡Jugar!")
    await delay(5000);

    await tryLoop(browser, page, 0)
        .then(pg => tryLoop(browser, pg, 1))
        .then(pg => tryLoop(browser, pg, 2))
        .then(pg => tryLoop(browser, pg, 3))
        .then(pg => tryLoop(browser, pg, 4))
        .then(pg => tryLoop(browser, pg, 5))

    await delay(5000)
    await page.screenshot({path: ("./results/fail_" + new Date().toISOString().split('T')[0] + ".png")})
    //await rl.close()
    await browser.close()
}

async function tryLoop(browser, page, index) {
    await possibleWords(index)
        .then(word => writteWord(page, word))
        .then(_ => clickButton(page, "enviar"))
        .then(_ => searchResults(browser, page, index))
        .then(_ => parseMatrix(index))
    return page
}

async function writteWord(page, word) {
  if (word == undefined) {
    console.log("Word not found! Any word matchs")
    this.process.exit(1)
  }
  console.log("Possible word: " + word)
  const letters = Array.from(word)
  for await (const letter of letters) {
    await clickButton(page, letter)
  }
}

async function clickButton(page, buttonText) {
    const search = "//button[text()='" + buttonText + "']"
    const [button] = await page.$x(search);
    await button.click()
    await delay(1000)
}

async function searchResults(browser, page, index) {
    const results = await page.evaluate(_ => {
        return Array.from(document.querySelectorAll('div.grid-cols-5'), row => Array.from(row.childNodes, 
            letterPosition => {
                const letter = letterPosition.childNodes[0].childNodes[1].childNodes[0]
                return [letter.textContent,
                    letter.classList.contains('bg-correct') ? "green" : 
                        letter.classList.contains('bg-present') ? "yellow" :
                            letter.classList.contains('bg-absent') ? "gray" : ""

                ]
            }))
        }
    )
    matrix = results
    console.log(matrix)
    cont = 0
    for (i = 0; i < 6; i++) {
        for (j = 0; j < 5; j++) {
            if (matrix[i][j][1] == "green")
                cont++
        }
        if (cont == 5) {
            console.log("Terminado! Palabra => " + matrix[i][0][0] + matrix[i][1][0] + matrix[i][2][0] + matrix[i][3][0] + matrix[i][4][0])
            await delay(5000)
            await page.screenshot({path: ("./results/correct_attemp_" + (index + 1) + "_" + new Date().toISOString().split('T')[0] + ".png")})
            //await rl.close()
            await browser.close()
            this.process.exit(1)
        } else
            cont = 0
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////

async function parseMatrix(index) {
    for(i = 0; i < 6; i++) {
        for(j = 0; j < 5; j++) {
            if (i == index && j == 0) {
                row = matrix[i]
                for(k = 0; k < 5; k++) {
                    if (row[k][1] == "green") {
                        startsWith += row[k][0]
                    } else {
                        break
                    }
                }
                for(k = 4; k > -1; --k) {
                    if (row[k][1] == "green") {
                        endsWith = row[k][0] + endsWith
                    } else {
                        break
                    }
                }
            }
            position = matrix[i][j]
            if (position[1] == "yellow" || position[1] == "green") {
                if (contains.indexOf(position[0]) === -1) {
                    contains.push(position[0])
                }
            } else if (position[1] == "gray") {
                if (notContains.indexOf(position[0]) === -1) {
                    notContains.push(position[0])
                }
            }
        }
    }

    contains.forEach(letter => {
        if(notContains.indexOf(letter) != -1)
            delete notContains[notContains.indexOf(letter)]
    })
    console.log("The word starts with => " + startsWith)
    console.log("The word ends with => " + endsWith)
    console.log("The word contains this letters => " + contains)
    console.log("The word not contains this letters => " + notContains)
    startsWith = ""
    endsWith = ""
}

async function possibleWords(index) {
    console.log("\nAttemp number #" + (index+1))
    if (index == 0) {
        return "cesta"
    } else {
        var words = fs.createReadStream(words5);
        var rl = readline.createInterface({
            input: words,
            crlfDelay: Infinity
        })
        const palabras = []
        for await (const word of rl) {
            if(word.startsWith(startsWith) && word.endsWith(endsWith) &&
                contains.every(letter => word.split('').includes(letter)) && !notContains.some(letter => word.split('').includes(letter)) &&
                await checkWordPosition(word.split(''), index)) {
                    palabras.push(word)
            }
        }
        console.log("Found " + palabras.length + " possible words")
        if (palabras.length > 1) {
            return palabras[Math.floor(Math.random() * palabras.length)]
        } else {
            return palabras[0]
        }
    }
}

async function checkWordPosition(letters, index) {
    for(row = 0; row < index; row++) {
        for (i = 0; i < letters.length; i++) {
            if (letters[i] == matrix[row][i][0] && (matrix[row][i][1] == "yellow" || matrix[row][i][1] == "gray"))
                return false
            if (letters[i] != matrix[row][i][0] && matrix[row][i][1] == "green")
                return false
        }
    }
    return true
}

start()
