using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO.Ports;
namespace SerialWrapper
{
    class Program
    {
        static void Main(string[] args)
        {
            String portName = Console.ReadLine();
            SerialPort p = new SerialPort(portName);
            p.BaudRate = 57600;
            p.Parity = Parity.None;
            p.Open();
            while (true)
            {
                String d = Console.ReadLine();
                if (d.Equals("Q")) break;
                p.Write(d + "#");
                if (d.Equals("R"))
                {
                    String s = "";
                    while (!s.Contains("-----END-----"))
                    {
                        s = p.ReadLine();
                        Console.WriteLine(s);
                   }
                } 
            }
        }
    }
}
