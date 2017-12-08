const cluster = require('cluster');
const numCPUs = require('os').cpus().length;

const C100_OUTPUT = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
const C1000_OUTPUT = C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT;
const C10000_OUTPUT = C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT;

const padResponse = false;

const forkSlaves = function() {
	if (cluster.isMaster) {
		console.log('Master ' + process.pid + ' is running');

		// Fork workers.
		for (var i = 0; i < numCPUs; i++) {
			cluster.fork();
		}

		cluster.on('exit', function(worker) {
			console.log('worker ' + worker.process.pid + ' died');
		});
	} else {
		// Workers can share any TCP connection
		// In this case it is an HTTP server
		createServer();

		console.log('Worker ' + process.pid +' started');
	}
};

const createServer = function() {
	const express = require('express');
	const app = express();

	app.get('/sum', function(req, res) {
		// Not yet implemented, setting default.
		// const delay = parseInt(req.query.delay, 10);
		// const delay = 0;
		const delay = NaN;

		// We don't really care about the value being returned, so saving CPU time.
		const result = req.query.nums.length;

        // const nums = (req.query.nums || "0");
		// var sum = 0;
		// for (var i=0; i<nums.length; i++) {
		//   sum += parseInt(nums[i], 10);
		// }
		// console.log("Replying " + sum);

		const respond = function() {
			return res.send("" + result + padResponse ? "\n" + C10000_OUTPUT : "");
		};

		if (isNaN(delay)) {
			respond();
		} else {
			setTimeout(respond, delay);
		}
	});

	const port = process.env.PORT || 3000;

	app.listen(port, function() {
		console.log('Number generator app listening on port ' + port + '.');
	});
};

forkSlaves();
