import Model from "./model.js";
const model = new Model();
const tags = [];
let x = 0;

//Hide all the content bodies and display the body passed to function
function hideAllBodiesExceptBody(id) {
  let tree = document.getElementById("home-body");
  if (id !== "home-body") tree.style.display = "none";

  tree = document.getElementById("view-body");
  if (id !== "view-body") tree.style.display = "none";

  tree = document.getElementById("ask-body");
  if (id !== "ask-body") tree.style.display = "none";

  tree = document.getElementById("answer-body");
  if (id !== "answer-body") tree.style.display = "none";

  tree = document.getElementById("tags-body");
  if (id !== "tags-body") tree.style.display = "none";

  tree = document.getElementById("search-body");
  if (id !== "search-body") tree.style.display = "none";

  const body = document.getElementById(id);
  body.style.display = "block";
}

//Convert date to a readable string for the website
function formateDate(askDate) {
  const date = new Date();
  const months = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec",
  ];
  let hrs = askDate.getHours();
  hrs = Math.floor(hrs);
  let hrsString = hrs < 10 ? "0" + hrs.toString() : hrs.toString();
  let mins = askDate.getMinutes();
  mins = Math.floor(mins);
  let minsString = mins < 10 ? "0" + mins.toString() : mins.toString();
  if (date.getFullYear() > askDate.getFullYear()) {
    return (
      months[askDate.getMonth()] +
      " " +
      askDate.getDate() +
      ", " +
      askDate.getFullYear() +
      " at " +
      hrsString +
      ":" +
      minsString
    );
  } else {
    const dateDiffMS = date - askDate;
    const dateDiffHours = dateDiffMS / (1000 * 60 * 60);
    if (dateDiffHours >= 24) {
      return (
        months[askDate.getMonth()] +
        " " +
        askDate.getDate() +
        " at " +
        hrsString +
        ":" +
        minsString
      );
    } else if (dateDiffHours >= 1) {
      return Math.floor(dateDiffHours) + " hours ago";
    } else if (dateDiffMS / (1000 * 60) >= 1) {
      return Math.floor(dateDiffMS / (1000 * 60)) + " minutes ago";
    } else {
      return Math.floor(dateDiffMS / 1000) + " seconds ago";
    }
  }
}

function updateTagCounts(tagIds) {
  for (let i = 0; i < tagIds.length; i++) {
    let push = 0;
    for (let j = 0; j < tags.length; j++) {
      if (parseInt(tagIds[i].slice(1)) - 1 === j) {
        push = 1;
        tags[j]++;
      }
    }

    if (push === 0) {
      tags.push(1);
    }
  }
}

function setupTagCounts() {
  //this only sets up the array, another function to dynamically update tags[]
  for (let i = 0; i < model.numTags(); i++) {
    let cnt = 0; //count of each tag
    for (let j = 0; j < model.numQuestions(); j++) {
      //count references of this tag in all questions
      for (let k = 0; k < model.numQuestionsTag(j); k++) {
        if (model.getTagId(i) === model.getQuestionTagId(j, k)) {
          cnt++;
        }
      }
    }

    tags.push(cnt);
  }
}

function constructQuestion(index) {
  const question = document.createElement("div");
  question.setAttribute("class", "homeQuestion");

  const statCol = document.createElement("div");
  statCol.innerHTML =
    model.numAnswers(index) + " answers<br>" + model.numViews(index) + " views";
  statCol.setAttribute("id", "statCol");

  const questionTitle = document.createElement("span");
  questionTitle.addEventListener("click", function () {
    viewQuestion(index);
  });
  questionTitle.setAttribute("id", "questionTitleView");
  questionTitle.innerHTML = model.getQuestionTitle(index);
  createTagsForQuestion(questionTitle, index); //deal with tags

  const metaCol = document.createElement("span");
  const asker = document.createElement("span");
  asker.innerHTML = model.getAsker(index);
  asker.style.fontSize = "large";
  asker.style.color = "red";
  asker.style.fontWeight = "bold";
  const askDate = document.createElement("span");
  askDate.innerHTML = " asked " + formateDate(model.getAskDate(index));
  metaCol.appendChild(asker);
  metaCol.appendChild(askDate);

  question.appendChild(statCol);
  question.appendChild(questionTitle);

  question.appendChild(metaCol);
  return question;
}

function createTagsForQuestion(mainColumn, idx) {
  const tags = document.createElement("span");
  for (let j = 0; j < model.numQuestionsTag(idx); j++) {
    const tag = document.createElement("span");
    tag.setAttribute("id", "indiTag");
    let qtagId = model.getQuestionTagId(idx, j);
    for (let k = 0; k < model.numTags(); k++) {
      let tagId = model.getTagId(k);
      if (qtagId === tagId) {
        tag.innerHTML = model.getTagName(k);

        // tag.addEventListener("click", function() {
        //   displayTagQuestions(model, tagId);
        // });
        tags.appendChild(tag);
        break;
      }
    }
  }
  mainColumn.appendChild(document.createElement("br"));
  mainColumn.appendChild(document.createElement("br"));
  mainColumn.appendChild(tags);
}

function newestQuestions() {
  document.getElementById("allquestions").innerHTML = "All Questions";
  document.getElementById("numQ").innerHTML = model.numQuestions();
  const questionsList = document.getElementById("homeQuestionsList");
  for (let i = model.numQuestions() - 1; i >= 0; i--) {
    const question = constructQuestion(i);
    questionsList.appendChild(question);
    questionsList.appendChild(document.createElement("br"));
    questionsList.appendChild(document.createElement("hr"));
  }
}

function newBtn() {
  const body = document.getElementById("homeQuestionsList");
  body.innerHTML = "";
  document.getElementById("allquestions").innerHTML = "All Questions";
  setupHome();
}

function actBtn() {
  // alert("Newest button clicked!");
  const questionsList = document.getElementById("homeQuestionsList");
  document.getElementById("numQ").innerHTML = model.numQuestions();
  document.getElementById("allquestions").innerHTML = "All Questions";
  questionsList.innerHTML = "";

  const activeQs = [];
  const unansQs = [];
  for (let i = 0; i < model.numQuestions(); i++) {
    if (model.numAnswers(i) === 0) {
      unansQs.push(i);
    } else {
      let obj = {
        index: i,
        id: model.getAnsIdFromQuestion(i, model.numAnswers(i) - 1),
      };

      activeQs.push(obj);
    }
  }

  activeQs.sort((a1, a2) => a2.id.slice(1) - a1.id.slice(1));
  for (let i = 0; i < activeQs.length; i++) {
    const question = constructQuestion(activeQs[i].index);
    questionsList.appendChild(question);
    questionsList.appendChild(document.createElement("br"));
    questionsList.appendChild(document.createElement("hr"));
  }

  //sort to be most recent first
  unansQs.sort((a, b) => b - a);
  for (let i = 0; i < unansQs.length; i++) {
    const question = constructQuestion(unansQs[i]);
    questionsList.appendChild(question);
    questionsList.appendChild(document.createElement("br"));
    questionsList.appendChild(document.createElement("hr"));
  }
}

function unansBtn() {
  // alert("Unanswered Button clicked!");
  const questionsList = document.getElementById("homeQuestionsList");
  document.getElementById("numQ").innerHTML = model.numQuestions();
  document.getElementById("allquestions").innerHTML = "All Questions";
  questionsList.innerHTML = "";

  const qIds = [];
  for (let i = 0; i < model.numQuestions(); i++) {
    if (model.numAnswers(i) === 0) {
      qIds.push(i);
    }
  }

  if (qIds.length === 0) {
    questionsList.innerHTML = "<h1>No Questions Found</h1>";
  } else {
    qIds.sort((a, b) => b - a);
    for (let i = 0; i < qIds.length; i++) {
      const question = constructQuestion(qIds[i]);
      questionsList.appendChild(question);
      questionsList.appendChild(document.createElement("br"));
      questionsList.appendChild(document.createElement("hr"));
    }
  }
}

function ansbtn() {
  resetForm();
  hideAllBodiesExceptBody("answer-body");
}

//Shows everything besides the questions themselves in home page content body
function displayHomePageMeta() {
  const qLink = document.getElementById("questionsLink");
  qLink.addEventListener("click", () => {
    linkClicked(0);
  });

  const tLink = document.getElementById("tagsLink");
  tLink.addEventListener("click", () => {
    linkClicked(1);
  });

  document.getElementById("allquestions").innerHTML = "All Questions";

  const askQuestionBtn = document.getElementById("askQuestion");
  askQuestionBtn.addEventListener("click", askQuestion);

  const numQuestionsTag = document.getElementById("numQ");
  numQuestionsTag.innerHTML = model.numQuestions();

  const newestBtn = document.getElementById("new");
  newestBtn.addEventListener("click", newBtn);

  const activeBtn = document.getElementById("act");
  activeBtn.addEventListener("click", actBtn);

  const unansweredBtn = document.getElementById("unans");
  unansweredBtn.addEventListener("click", unansBtn);
}

function getNumRows() {
  let full = Math.floor(model.numTags() / 3);
  if (model.numTags() % 3 !== 0) full++;
  return full;
}

function displayTags(tags) {
  let qIds = [];
  let tagA = [tags];
  //console.log("Tags : " + tagA);
  tagSearch(tagA, qIds);
  // console.log(qIds);
  hideAllBodiesExceptBody("home-body");
  document.getElementById("numQ").innerHTML = qIds.length;
  const que = document.getElementById("homeQuestionsList");
  que.innerHTML = "";
  for (let i = qIds.length - 1; i >= 0; i--) {
    let index = parseInt(qIds[i].slice(1)) - 1;
    const q = constructQuestion(index);
    que.appendChild(q);
    que.appendChild(document.createElement("br"));
    que.appendChild(document.createElement("hr"));
  }
  updateSideBar(0);

  //const homePage = document.getElementBy
}

function addClickLink(l, tagName) {
  // console.log("Here : " + tagName);
  l.addEventListener("click", () => {
    displayTags(tagName);
  });
}

function displayTagPage() {
  hideAllBodiesExceptBody("tags-body");
  const tagsBody = document.getElementById("tags-body");
  tagsBody.innerHTML = "";

  const topRow = document.createElement("div");
  topRow.setAttribute("id", "topRow");

  const ntags = document.createElement("span");
  ntags.setAttribute("id", "totalTags");
  ntags.innerHTML = model.numTags() + " Tags";

  const all = document.createElement("span");
  all.setAttribute("id", "allTags");
  all.innerHTML = "All Tags";

  const askbtn = document.createElement("button");
  askbtn.innerHTML = "Ask Question";
  askbtn.addEventListener("click", askQuestion);
  askbtn.setAttribute("id", "askQuestionTag");

  topRow.appendChild(ntags);
  topRow.appendChild(all);
  topRow.appendChild(askbtn);

  tagsBody.appendChild(topRow);

  const main = document.createElement("div");
  main.setAttribute("id", "mainTag");
  tagsBody.appendChild(main);
  let rowNum = getNumRows();

  let ind = 0;
  let count = 0;
  for (let i = 0; i < rowNum; i++) {
    const row = document.createElement("div");
    row.setAttribute("id", "tagRow");
    if (model.numTags() > count) {
      let end = 3;
      if (model.numTags() - count < 3) end = model.numTags() - count;

      for (let j = 0; j < end; j++) {
        const elem = document.createElement("div");
        elem.setAttribute("id", "tagElem");

        const link = document.createElement("div");
        link.setAttribute("id", "tagLink");
        link.innerHTML = model.getTagName(ind + j);

        //Link to function, maybe pass in the name (getTagName(ind + j)) or index (ind + j) to function
        //link.addEventListener("click", () => {
        //func(ind + j)
        //});

        addClickLink(link, model.getTagName(ind + j));

        const tagNum = document.createElement("div");
        tagNum.setAttribute("id", "tagN");
        if (tags[ind + j] > 1) tagNum.innerHTML = tags[ind + j] + " questions";
        else tagNum.innerHTML = tags[ind + j] + " question";

        elem.appendChild(link);
        elem.appendChild(tagNum);

        row.appendChild(elem);
      }
      count += 3;
      ind += 3;
    }
    main.appendChild(row);
  }
}

//View a question, view content body
function viewQuestion(index) {
  x = index;
  updateSideBar(-1);
  hideAllBodiesExceptBody("view-body");
  const contentBody = document.getElementById("view-body");
  contentBody.innerHTML = ""; //Clear previous if any questions that were displayed

  model.updateQuestionViews(index); //update views

  const metaRow = document.createElement("div");
  metaRow.setAttribute("id", "metaRow");

  const numAnswers = document.createElement("span");
  numAnswers.innerHTML = model.numAnswers(index) + " answers";
  numAnswers.setAttribute("id", "numAnswers");

  const questionTitle = document.createElement("span");
  questionTitle.innerHTML = model.getQuestionTitle(index);
  questionTitle.setAttribute("id", "questionTitle");
  const askbtn = document.createElement("button");
  askbtn.innerHTML = "Ask Question";
  askbtn.addEventListener("click", askQuestion);
  askbtn.setAttribute("id", "askQuestion");
  askbtn.style.marginTop = "0%";

  metaRow.appendChild(numAnswers);
  metaRow.appendChild(questionTitle);
  metaRow.appendChild(askbtn);
  contentBody.appendChild(metaRow);

  const detailRow = document.createElement("div");
  const numViews = document.createElement("span");
  numViews.innerHTML = model.numViews(index) + " views";
  numViews.setAttribute("id", "numViews");

  const questionDetails = document.createElement("span");
  questionDetails.innerHTML = model.questionDetails(index);
  questionDetails.setAttribute("id", "questionDetails");

  const asker = document.createElement("span");
  asker.innerHTML = model.getAsker(index);
  const date = document.createElement("span");
  date.innerHTML = "<br>asked " + formateDate(model.getAskDate(index));
  date.style.color = "gray";
  asker.setAttribute("id", "asker");
  asker.appendChild(date);

  detailRow.appendChild(numViews);
  detailRow.appendChild(questionDetails);
  detailRow.appendChild(asker);
  contentBody.appendChild(detailRow);

  contentBody.appendChild(document.createElement("br"));
  contentBody.appendChild(document.createElement("br"));
  contentBody.appendChild(document.createElement("hr"));

  const answerList = document.createElement("div");
  const ansList = model.getQuestionAnswers(index);
  ansList.sort((a, b) => {
    if (a < b) return 1;
    if (a > b) return -1;
    return 0;
  });

  for (let i = 0; i < ansList.length; i++) {
    const answer = document.createElement("div");
    const textCol = document.createElement("span");
    textCol.setAttribute("id", "ansText");

    const user = document.createElement("span");
    user.setAttribute("id", "userNameAns");

    const ansIdx = parseInt(ansList[i].slice(1)) - 1;
    // console.log(ansIdx);
    textCol.innerHTML = model.getAnswerText(ansIdx);
    user.innerHTML = model.getAnswerUser(ansIdx);
    const ask = document.createElement("span");
    ask.innerHTML = "<br>answered " + formateDate(model.getAnswerDate(ansIdx));
    ask.style.color = "gray";
    ask.style.fontWeight = "normal";

    user.appendChild(ask);
    answer.appendChild(textCol);
    answer.appendChild(user);
    answer.appendChild(document.createElement("hr"));
    answerList.appendChild(answer);
  }

  // let j = 0;
  // for (let i = 0; i < model.numAnswers(index); i++) {
  //   const answer = document.createElement("div");
  //   const textCol = document.createElement("span");
  //   textCol.setAttribute("id", "ansText");

  //   const user = document.createElement("span");
  //   user.setAttribute("id", "userNameAns");

  // for (; j < model.getAnswersLength(); j++) {
  //   if (model.getAnsIdFromQuestion(index, i) === model.getAnsId(j)) {
  //     textCol.innerHTML = model.getAnswerText(j);
  //     user.innerHTML = model.getAnswerUser(j);
  //     const ask = document.createElement("span");
  //     ask.innerHTML = "<br>answered " + formateDate(model.getAnswerDate(j));
  //     ask.style.color = "gray";
  //     ask.style.fontWeight = "normal";
  //     user.appendChild(ask);
  //     break;
  //   }
  // }

  //   answer.appendChild(textCol);
  //   answer.appendChild(user);
  //   answer.appendChild(document.createElement("hr"));
  //   answerList.appendChild(answer);
  // }
  answerList.appendChild(document.createElement("br"));

  contentBody.appendChild(answerList);
  const answerBtn = document.createElement("button");
  answerBtn.innerHTML = "Answer Question";
  answerBtn.addEventListener("click", function () {
    ansbtn(index);
  });
  answerBtn.setAttribute("id", "answerBtn");
  contentBody.appendChild(answerBtn);

  /*const post = document.getElementById("postAns");
  post.addEventListener("click", () => {
    postAnswer(index);
  });*/
}

function setupHome() {
  displayHomePageMeta();
  document.getElementById("allquestions").innerHTML = "All Questions";
  newestQuestions();
}

function displayHomePage() {
  hideAllBodiesExceptBody("home-body");
  const questionsList = document.getElementById("homeQuestionsList");
  questionsList.innerHTML = "";
  newestQuestions();
}

function checkTag(tagName) {
  for (let i = 0; i < model.numTags(); i++) {
    if (tagName.toLowerCase() === model.getTagName(i).toLowerCase()) return 1;
  }
}

function postAnswer(ind) {
  let error = 0;
  if (document.getElementById("qtTextAns").value.length === 0) {
    document.getElementById("qtAnsError").innerHTML =
      "Username Can Not Be Empty";
    error = 1;
  } else {
    document.getElementById("qtAnsError").innerHTML = "";
  }
  if (document.getElementById("qtextTextAns").value.length === 0) {
    document.getElementById("qtTextAnsError").innerHTML =
      "Question Text Can Not Be Empty";
    error = 1;
  } else {
    document.getElementById("qtTextAnsError").innerHTML = "";
  }

  if (error != 1) {
    let newAid = "a" + (model.numAnswersTotal() + 1);
    const ans = {
      aid: newAid,
      text: document.getElementById("qtextTextAns").value,
      ansBy: document.getElementById("qtTextAns").value,
      ansDate: new Date(),
    };
    model.pushAnswerId(ind, newAid);
    model.pushAnswer(ans);
    // console.log(model.data.answers);
    // console.log(model.data.questions[ind]);
    viewQuestion(ind);
  }
}

function postQuestion() {
  let error = 0;
  const nonDuplicate = [];
  if (document.getElementById("qtText").value.length > 100) {
    document.getElementById("qtError").innerHTML = "Too Many Characters";
    error = 1;
  } else {
    document.getElementById("qtError").innerHTML = "";
  }
  if (document.getElementById("qtText").value.length === 0) {
    document.getElementById("qtError").innerHTML =
      "Question Title Can Not Be Empty";
    error = 1;
  } else {
    document.getElementById("qtError").innerHTML = "";
  }
  if (document.getElementById("qtextText").value.length === 0) {
    document.getElementById("qtTextError").innerHTML =
      "Question Text Can Not Be Empty";
    error = 1;
  } else {
    document.getElementById("qtTextError").innerHTML = "";
  }
  if (document.getElementById("userText").value.length === 0) {
    document.getElementById("userTextError").innerHTML =
      "Username Can Not Be Empty";
    error = 1;
  } else {
    document.getElementById("userTextError").innerHTML = "";
  }

  if (document.getElementById("tagsText").value.length !== 0) {
    let lenError = 0;
    const tags = document
      .getElementById("tagsText")
      .value.toLowerCase()
      .split(" ");
    nonDuplicate[0] = tags[0];
    for (let i = 0; i < tags.length; i++) {
      let include = 0;
      for (let j = 0; j < nonDuplicate.length; j++) {
        if (nonDuplicate[j] === tags[i]) include = 1;
      }
      if (include === 0 && tags[i].length <= 20) nonDuplicate.push(tags[i]);
      else if (tags[i].length > 20) {
        lenError = 1;
      }
    }
    if (nonDuplicate.length > 5 && lenError === 1) {
      document.getElementById("tagsTextError").innerHTML =
        "Only 5 Unique Tags Allowed and Tags Can Not Have More Than 20 Characters";
      error = 1;
    } else if (nonDuplicate.length > 5) {
      document.getElementById("tagsTextError").innerHTML =
        "Only 5 Unique Tags Allowed";
      error = 1;
    } else if (lenError === 1) {
      document.getElementById("tagsTextError").innerHTML =
        "Tags Can Not Have More Than 20 Characters";
      error = 1;
    } else {
      document.getElementById("tagsTextError").innerHTML = "";
    }
  }

  for (let i = 0; i < nonDuplicate.length; i++) {
    if (checkTag(nonDuplicate[i]) !== 1) {
      let newTid = "t" + (model.numTags() + 1);
      const newTag = {
        tid: newTid,
        name: nonDuplicate[i],
      };
      model.pushTag(newTag);
    }
  }

  let tagIds = getTagIds(nonDuplicate);

  if (error != 1) {
    let newQid = "q" + (model.numQuestions() + 1);
    const newQuestion = {
      qid: newQid,
      title: document.getElementById("qtText").value,
      text: document.getElementById("qtextText").value,
      tagIds: tagIds,
      askedBy: document.getElementById("userText").value,
      askDate: new Date(),
      ansIds: [],
      views: 0,
    };

    model.pushQuestion(newQuestion);
    //update tag counts
    updateTagCounts(tagIds);

    const side = document.getElementById("sidebar");
    side.style.marginRight = "0%";
    const numQuestionsTag = document.getElementById("numQ");
    numQuestionsTag.innerHTML = model.numQuestions();
    displayHomePage();
    // console.log(model.data.questions);
    // console.log(model.data.tags);
  }
}

function getTagIds(arrayTags) {
  let arr = [];
  for (let i = 0; i < model.numTags(); i++) {
    for (let j = 0; j < arrayTags.length; j++) {
      if (arrayTags[j] === model.getTagName(i).toLowerCase())
        arr.push(model.getTagId(i));
    }
  }
  return arr;
}

function updateSideBar(param) {
  const x = document.getElementById("questionsLink");
  const y = document.getElementById("tagsLink");
  if (param === 0) {
    x.style.backgroundColor = "gray";
    x.style.color = "white";
    y.style.backgroundColor = "white";
    y.style.color = "gray";
    const side = document.getElementById("sidebar");
    side.style.marginRight = "0%";
  } else if (param === 1) {
    x.style.backgroundColor = "white";
    x.style.color = "gray";
    y.style.backgroundColor = "gray";
    y.style.color = "white";
    const side = document.getElementById("sidebar");
    side.style.marginRight = "0%";
  } else {
    x.style.backgroundColor = "white";
    x.style.color = "gray";
    y.style.backgroundColor = "white";
    y.style.color = "gray";
  }
}

function linkClicked(qOrT) {
  //console.log("Hello " + qOrT);
  if (qOrT === 0) {
    displayHomePage();
  } else {
    displayTagPage();
  }
  updateSideBar(qOrT);
}

function resetForm() {
  document.getElementById("qtText").value = "";
  document.getElementById("qtTextAns").value = "";
  document.getElementById("qtTextAnsError").value = "";
  document.getElementById("qtextText").value = "";
  document.getElementById("qtextTextAns").value = "";
  document.getElementById("tagsText").value = "";
  document.getElementById("userText").value = "";
  document.getElementById("qtError").innerHTML = "";
  document.getElementById("qtAnsError").innerHTML = "";
  document.getElementById("qtTextError").innerHTML = "";
  document.getElementById("tagsTextError").innerHTML = "";
  document.getElementById("userTextError").innerHTML = "";
}

function askQuestion() {
  const side = document.getElementById("sidebar");
  side.style.marginRight = "2%";
  hideAllBodiesExceptBody("ask-body");
  resetForm();
  const post = document.getElementById("post");
  post.addEventListener("click", postQuestion);
  updateSideBar(-1);
}

//Search list contains words, questionIds is the list of search results of question ids
function titleSearch(searchList, questionIds) {
  for (let i = 0; i < searchList.length; i++) {
    let term = searchList[i];
    term = term.toLowerCase(); // Just in case
    for (let j = 0; j < model.numQuestions(); j++) {
      let questionTitle = model.getQuestionTitle(j);
      questionTitle = questionTitle.toLowerCase();
      if (questionTitle.indexOf(term) !== -1) {
        let qid = model.getQuestionId(j);
        if (questionIds.indexOf(qid) === -1) {
          questionIds.push(qid);
        }
      }
    }
  }
}

function textSearch(searchList, questionIds) {
  for (let i = 0; i < searchList.length; i++) {
    let term = searchList[i];
    term = term.toLowerCase();
    for (let j = 0; j < model.numQuestions(); j++) {
      let qtext = model.questionDetails(j);
      qtext = qtext.toLowerCase();
      if (qtext.indexOf(term) !== -1) {
        let qid = model.getQuestionId(j);
        if (questionIds.indexOf(qid) === -1) {
          // console.log(qid);
          questionIds.push(qid);
        }
      }
    }
  }
}

function tagSearch(searchList, questionIds) {
  let tagIds = getTagIds(searchList); //this gets the tag ids given the tags
  for (let i = 0; i < tagIds.length; i++) {
    let tid = tagIds[i];
    for (let j = 0; j < model.numQuestions(); j++) {
      for (let k = 0; k < model.numQuestionsTag(j); k++) {
        if (tid === model.getQuestionTagId(j, k)) {
          let qid = model.getQuestionId(j);
          if (questionIds.indexOf(qid) === -1) {
            // console.log(qid +":"+k);
            questionIds.push(qid);
          }

          break;
        }
      }
    }
  }
}

function processSearchInput(input) {
  let taglist = [];
  let textlist = [];
  let processedInput = [taglist, textlist];
  let termsArray = input.split(" "); //split on space
  for (let i = 0; i < termsArray.length; i++) {
    let term = termsArray[i];
    term = term.toLowerCase();
    if (term.indexOf("[") !== -1 || term.indexOf("]") !== -1) {
      taglist.push(term.slice(1, term.length - 1));
    } else {
      textlist.push(term);
    }
  }

  return processedInput;
}

function displaySearchResults(qids) {
  hideAllBodiesExceptBody("home-body");
  document.getElementById("allquestions").innerHTML = "Search Results";
  document.getElementById("numQ").innerHTML = qids.length;
  const qlist = document.getElementById("homeQuestionsList");

  if (qids.length === 0) {
    qlist.innerHTML = "<h1>No Questions Found</h1>";
    return;
  }

  qlist.innerHTML = "";
  qids.sort((a, b) => {
    if (a < b) return 1;
    if (a > b) return -1;
    return 0;
  });

  for (let i = 0; i < qids.length; i++) {
    let idx = parseInt(qids[i].slice(1)) - 1;
    // console.log(idx);
    const question = constructQuestion(idx);
    qlist.appendChild(question);
    qlist.appendChild(document.createElement("br"));
    qlist.appendChild(document.createElement("hr"));
  }
}

function search(event) {
  const inputTag = document.getElementById("search-Bar");
  if (event.keyCode === 13) {
    // for(let i=0; i<model.numQuestions(); i++) {
    //   console.log(model.getQuestionId(i));
    // }

    let input = inputTag.value;
    let processedInput = processSearchInput(input);
    let tagsList = processedInput[0];
    let textuals = processedInput[1];
    let qids = []; //array of all question ids satisfying the results
    titleSearch(textuals, qids);
    textSearch(textuals, qids);
    tagSearch(tagsList, qids);
    displaySearchResults(qids);
    inputTag.value = "Search ...";

    // for(let i=0; i<model.numQuestions(); i++) {
    //   console.log(model.getQuestionTagId(i));
    // }
    // console.log(qids);
    // console.log(processedInput);
  }
}

window.onload = function () {
  hideAllBodiesExceptBody("home-body");
  const post = document.getElementById("postAns");
  post.addEventListener("click", () => {
    postAnswer(x);
  });
  setupHome();
  setupTagCounts();
  document.getElementById("search-Bar").addEventListener("keyup", search);
};
