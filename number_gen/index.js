const cluster = require('cluster');
const http = require('http');
const numCPUs = require('os').cpus().length;

const forkSlaves = function() {
	if (cluster.isMaster) {
		console.log(`Master ${process.pid} is running`);

		// Fork workers.
		for (let i = 0; i < numCPUs; i++) {
			cluster.fork();
		}

		cluster.on('exit', (worker, code, signal) => {
			console.log(`worker ${worker.process.pid} died`);
		});
	} else {
		// Workers can share any TCP connection
		// In this case it is an HTTP server
		createServer();

		console.log(`Worker ${process.pid} started`);
	}
};

const createServer = function() {
	const express = require('express');
	const app = express();

	app.get('/sum', function(req, res) {
		//const num1Str = req.query.num1 || "0";
		//const num2Str = req.query.num2 || "0";
		//const nums = [num1Str, num2Str];
		//const nums = (req.query.nums || "0").split(",");
		//const delay = parseInt(req.query.delay, 10);
		const delay = 0;
		// const delay = NaN;
		//const sum = nums.length;
		// Simlplifying calculation temporarily.
		const sum = req.query.nums.length;
		//var sum = 0;
		//for (var i=0; i<nums.length; i++) {
			//sum += parseInt(nums[i], 10);
		//}
		//console.log("Replying " + sum);
		const respond = function() { return res.send("" + sum); };
		if (isNaN(delay)) {
			respond();
		} else {
			setTimeout(respond, delay);
		}
	});

	const port = process.env.PORT || 3000;

	app.listen(port, function() {console.log('Number generator app listening on port ' + port + '.');});
};

forkSlaves();
