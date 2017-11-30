const express = require('express');
const app = express();

app.get('/sum', (req, res) => {
  //const num1Str = req.query.num1 || "0";
  //const num2Str = req.query.num2 || "0";
  //const nums = [num1Str, num2Str];
  const nums = (req.query.nums || "0").split(",");
  const delay = parseInt(req.query.delay, 10);
  let sum = 0;
  for (let i=0; i<nums.length; i++) {
    sum += parseInt(nums[i], 10);
  }
  console.log("Replying " + sum);
  const respond = () => res.send("" + sum);
  if (isNaN(delay)) {
    respond();
  } else {
    setTimeout(respond, delay);
  }
});

const port = process.env.PORT || 3000;

app.listen(port, () => console.log('Number generator app listening on port ' + port + '.'));
